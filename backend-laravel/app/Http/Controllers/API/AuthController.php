<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;
use Illuminate\Support\Carbon;
use Illuminate\Support\Facades\Validator;
use App\Models\User;
use App\Http\Controllers\API\BaseController;
use App\Models\Role;
use App\Notifications\ForgotPassword;
use Illuminate\Validation\ValidationException;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Notification;
use App\Services\FirebaseNotification;
use Illuminate\Auth\Events\Registered;

class AuthController extends BaseController
{
    /**
     * Register api
     *
     * @return \Illuminate\Http\Response
     */
    protected $expiresAt;
    protected $abilities;
    protected $tokenName;
    protected $firebaseNotificationService;
    public function __construct(FirebaseNotification $firebaseNotificationService)
    {
        $this->expiresAt = Carbon::now()->addDays(365);
        $this->abilities = ['*'];
        $this->tokenName = 'AccessToken';
        $this->firebaseNotificationService = $firebaseNotificationService;
    }
    public function register(Request $request): JsonResponse
    {
        //TODO username change as name
        $validator = Validator::make($request->all(), [
            'name' => 'required',
            'email' => 'required|email|unique:users',
            'password' => 'required|string|min:6',
            // 'username' => 'required|unique:users,username',
            'phone_number' => 'required|min:10|unique:users,phone_number',
            'device_token' => 'required',
        ]);
        if ($validator->fails()) {
            return $this->sendError($validator->errors()->first(), ['error' => $validator->errors()->first()], 400);
        }
        $input = $request->all();
        $user = new User($input);
        $user->name = $input['name'];
        $user->email = $input['email'];
        $user->password = bcrypt($input['password']);
        $user->profile_pic = '';
        $user->image = '';
        $user->phone_number = $input['phone_number'];
        $user->device_token = $input['device_token'];
        $user->role_id = 2; //User Role
        $user->is_blocked = 0;
        $user->save();
        //Second way to send notification
        // $user->sendEmailVerificationNotification();
        event(new Registered($user));
        // $success['token'] =  $user->createToken($this->tokenName, $this->abilities, $this->expiresAt)->plainTextToken;
        $success['name'] =  $user->name;
        $success['role'] = Role::where('id', 2)->get()->first();
        return $this->sendResponse($success, 'User register successfully.', 200);
    }
    public function login(Request $request): JsonResponse
    {
        $validator = Validator::make($request->all(), [
            'email' => 'required|email',
            'password' => 'required',
            'device_token' => 'required',
        ]);
        if ($validator->fails()) {
            return $this->sendError($validator->errors()->first(), ['error' =>  $validator->errors()->first()], 400);
        }
        $isUserExits = User::where('email', $request->email)->get();

        if (is_null($isUserExits)) {
            return $this->sendError('No user found with this email.', ['error' => 'No user found with this email'], 404);
        }
        //FIXME Block check
        //FIXME Password error
        //FIXME REmove hardcode message
        if (Auth::attempt(['email' => $request->email, 'password' => $request->password])) {
            $user = User::where('id', Auth::user()->id)->with('role')->get()->first();
            if (!auth()->user()->hasVerifiedEmail()) {
                $user->sendEmailVerificationNotification();
                return $this->sendError('वेरिफिकेशन के लिए अपना ईमेल चेक करें।', ['error' => 'वेरिफिकेशन के लिए अपना ईमेल चेक करें।'], 403);
            }
            $user->device_token = $request->device_token;
            $user->save();
            $user->token = $user->createToken($this->tokenName, $this->abilities, $this->expiresAt)->plainTextToken;
            return $this->sendResponse($user, 'User login successfully.');
        } else {
            return $this->sendError('Incorrect password', ['error' => 'Incorrect password.'], 401);
        }
    }
    public function mobileLogin(Request $request): JsonResponse
    {
        $validator = Validator::make($request->all(), [
            'phone_number' => 'required',
        ]);
        if ($validator->fails()) {
            return $this->sendError($validator->errors()->first(), ['error' => $validator->errors()->first()], 400);
        }
        $input = $request->all();
        $user = User::where('phone_number', $request->phone_number)->get()->first();
        if (!is_null($user)) {
            //TODO Send token
            // $success['token'] =  $user->createToken($this->tokenName, $this->abilities, $this->expiresAt)->plainTextToken;
            // $success['name'] =  $user->name;
            $success['id'] =  $user->id;
            return $this->sendResponse($success, 'User register successfully.', 200);
        }
        $user = new User($input);
        $user->name = '';
        $user->email = '';
        $user->password = bcrypt($input['phone_number']);
        $user->profile_pic = '';
        $user->image = '';
        $user->phone_number = $input['phone_number'];
        $user->otp = '1234';
        $user->role_id = 2; //User Role`
        $user->save();
        //FIXME here return 
        // $success['token'] =  $user->createToken($this->tokenName, $this->abilities, $this->expiresAt)->plainTextToken;
        $success['id'] =  $user->id;
        return $this->sendResponse($success, 'User register successfully.', 200);
    }
    public function mobileOTPVerification(Request $request): JsonResponse
    {
        $validator = Validator::make($request->all(), [
            'id' => 'required',
            'device_token' => 'required',
            'otp' => 'required',
        ]);
        if ($validator->fails()) {
            return $this->sendError($validator->errors()->first(), ['error' => $validator->errors()->first()], 400);
        }
        $user = User::where('id', $request->id)->first();
        if (is_null($user)) {
            return $this->sendError('No user found with this phone number.', ['error' => 'No user found with this phone number'], 404);
        }
        //FIXME Later otp 
        // if (strval($user->otp) != strval($request->otp)) {
        //     return $this->sendError('Otp not match.', ['error' => 'Otp Not match'], 401);
        // }
        $user->device_token =  $request->device_token;
        $user->save();
        $user = User::where('id', $user->id)->with('role')->get()->first();
        $user->token = $user->createToken($this->tokenName, $this->abilities, $this->expiresAt)->plainTextToken;
        return $this->sendResponse($user, 'User register successfully.', 200);
    }
    public function logout(Request $request): JsonResponse
    {
        $request->user()->tokens()->delete();
        return $this->sendResponse(null, 'Logged out successfully.');
    }
    public function forgetPassword(Request $request): JsonResponse
    {
        $validator = Validator::make($request->all(), [
            'email' => 'required',
        ]);
        if ($validator->fails()) {
            return $this->sendError($validator->errors()->first(), ['error' => $validator->errors()->first()], 400);
        }
        $user = User::where('email', $request->email)->first();
        if (is_null($user)) {
            return $this->sendError('No user found with this email', ['error' => 'No user found with this email'], 404);
        }
        $password = generatePassword(8);
        $newPassword = Hash::make($password);
        $user->update(['password' => $newPassword]);
        $user->notify(new ForgotPassword($user, $password));
        // Notification::route('mail', $user->email)->notify(new ForgotPassword($user, $password));
        return $this->sendResponse(null, 'Email send successfully.', 200);
    }


    public function blockUser(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'user_id' => 'required',
        ]);
        if ($validator->fails()) {
            return $this->sendError($validator->errors()->first(), ['error' => $validator->errors()->first()], 400);
        }
        $user = User::where('id', $request->user_id)->get()->first();
        if (is_null($user)) {
            return $this->sendError('No user found with this id', ['error' => 'No user found with this id'], 404);
        }
        if ($user && $user->is_blocked) {
            $user->is_blocked = 0;
            $user->save();
            return $this->sendResponse(null, 'User unblocked', 204);
        }
        $user->is_blocked = 1;
        $user->save();
        $tokens = User::where(['id' => $user->user_id])->whereNotNull('device_token')->where('device_token', '!=', '')->pluck('device_token')->toArray();
        $title = "You are blocked";
        $body = "You have been blocked by an administrator, please contact support to unblock your account";
        $data = [
            'title' => $title,
            'message' =>  $body,
            'image' => "",
            'id' => "",
            'type' => "user-blocked",
            'subType' => "user-blocked",
        ];
        if (!is_null($tokens)) {
            $this->firebaseNotificationService->sendToMultipleDevices($tokens, $title, $body, $data);
        }
        return $this->sendResponse(null, 'User blocked', 201);
    }
}
