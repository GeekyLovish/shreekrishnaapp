<?php

namespace App\Http\Controllers\Auth;

use App\Http\Controllers\Controller;
use App\Providers\RouteServiceProvider;
use Illuminate\Foundation\Auth\VerifiesEmails;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Auth\Events\Verified;
use Illuminate\Http\RedirectResponse;
use Illuminate\Foundation\Auth\EmailVerificationRequest;
use App\Models\User;

class VerificationController
{
    /*
    |--------------------------------------------------------------------------
    | Email Verification Controller
    |--------------------------------------------------------------------------
    |
    | This controller is responsible for handling email verification for any
    | user that recently registered with the application. Emails may also
    | be re-sent if the user didn't receive the original email message.
    |
    */
    public function verify($id, $hash, Request $request)
    {
        $user = User::find($id);
        if (!$user) {
            session()->flash('success-message', 'User not found.');
            return redirect()->route('home');
        }
        // Verify if the hash is correct
        if (!hash_equals((string) $hash, sha1($user->getEmailForVerification()))) {
            session()->flash('success-message', 'Invalid verification link.');
            return redirect()->route('home');
        }
        // Mark email as verified
        if (!$user->hasVerifiedEmail()) {
            $user->markEmailAsVerified();
        }
        session()->flash('success-message', 'Email verified successfully!');
        return redirect()->route('home');
    }

    // Resend the email verification notification
    public function resend(Request $request)
    {
        $user = User::where('email', $request->email)->first();
        if (!$user) {
            return response()->json(['message' => ''], 404);
            session()->flash('error-message', 'User not found.');
            return redirect()->route('home');
        }
        if ($user->hasVerifiedEmail()) {
            session()->flash('success-message', 'Email already verified.');
            return redirect()->route('home');
        }
        $request->user()->sendEmailVerificationNotification();
        session()->flash('success-message', 'A fresh verification link has been sent to your email address');
        return redirect()->back();
    }
}
