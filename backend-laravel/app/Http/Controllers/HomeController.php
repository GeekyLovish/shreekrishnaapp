<?php

namespace App\Http\Controllers;

use App\Models\Comment;
use App\Models\CommentLike;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;
use App\Models\Post;
use App\Models\PostLike;
use App\Notifications\DeleteAccount;

class HomeController extends Controller
{
    public function index(Request $request)
    {
        $data = null;
        return view('account-delete', compact("data"));
    }
    public function deleteRequest(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'email' => 'nullable|email', // Email is optional, but if present, it should be valid
            'mobile' => 'nullable|regex:/^(\+91)?[0-9]{1}[0-9]{9}$/', // Mobile is optional, but if present, it should be a valid 10-digit number
            'reason' => 'required|string', // Reason is required and must be a string
        ]);

        $data = null;
        // Custom validation rule to ensure that either email or mobile is provided
        $validator->after(function ($validator) use ($request) {
            $email = trim($request->email);  // Remove any whitespace
            $mobile = trim($request->mobile); // Remove any whitespace
            // If both email and mobile are empty, trigger the validation error
            if (empty($email) && empty($mobile)) {
                $message = "Either email or mobile number is required.";
                $validator->errors()->add('contact', $message);
                session()->flash('error-message', $message);
            }
        });
        if ($validator->fails()) {
            session()->flash('error-message', $validator->errors()->first());
            return redirect()->back()->withErrors($validator)->withInput();
        }
        if ($request->email) {
            $user = User::where("email", $request->email)->get()->first();
            if (!$user) {
                session()->flash('error-message', "User not found with this email address.");
                return redirect()->back();
            }
            $otp = generateOTP(6);
            $user->otp = $otp;
            $user->save();
            $user->notify(new DeleteAccount($user, $otp));
            $data = $user;
        } else if ($request->mobile) {
            $user = User::where("phone_number", $request->mobile)->get()->first();
            if (!$user) {
                session()->flash('error-message', "User not found with this mobile number.");
                return redirect()->back();
            }
            $user->otp = generateOTP(6);
            $user->save();
            $data = $user;
        } else {
            session()->flash('error-message', "No user found with this email or mobile number.");
            return redirect()->back();
        }
        return view('account-delete', compact("data"));
    }
    public function confirmDelete(Request $request, $id)
    {
        $data = User::where("id", $id)->get()->first();
        if (!$data) {
            session()->flash('error-message', "User not found.");
            return redirect()->back();
        }
        $validator = Validator::make($request->all(), [
            'confirm_delete' => 'required|string',
            'otp' => 'required|string',
        ]);

        if ($validator->fails()) {
            session()->flash('error-message', $validator->errors()->first());
            return view('account-delete', compact("data"));
        }
        if (strval($data->otp) != strval($request->otp)) {
            session()->flash('error-message', 'Otp not match');
            return view('account-delete', compact("data"));
        }


        $commentIDs = Comment::where('user_id', $id)->get()->select("id")->toArray();
        CommentLike::where('user_id', $id)->delete();
        CommentLike::whereIn('comment_id', $commentIDs)->delete();
        Comment::whereIn('id', $commentIDs)
            ->whereNotNull('comment_id')
            ->delete();
        Comment::where("id", $commentIDs)->delete();

        $postID = Post::where("user_id", $id)->get()->select("id")->toArray();

        PostLike::whereIn("post_id", $postID)->delete();
        Post::whereIn("id", $postID)->delete();
        $data->delete();
        $data = null;
        session()->flash('success-message', 'Your account and account data have been successfully deleted.');
        return redirect()->back();
    }
}
