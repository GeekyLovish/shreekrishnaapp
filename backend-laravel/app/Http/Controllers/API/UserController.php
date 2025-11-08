<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\API\BaseController;
use Illuminate\Http\Request;
use App\Models\User;
use Illuminate\Support\Facades\Storage;
use Intervention\Image\Laravel\Facades\Image;
use Illuminate\Support\Str;

class UserController extends BaseController
{
    public function updateProfile(Request $request)
    {
        $requestUser = $request->user();
        $user = User::where('id', $requestUser->id)->get()->first();
        if (is_null($user)) {
            return $this->sendError('User not found.', ['error' => 'User not found.'], 404);
        }
        $user->name = !empty($request->name) ? $request->name : $user->name;
        if ($request->hasFile('profile')) {
            $file = $request->file('profile'); // Use $request->file() to get the file
            $path = $file->store('users', 'public'); // Store in 'public/audio'
            $url = Storage::disk('public')->url($path);
            //thumbnail
            $thumbnailUrl = $this->createThumbnail($file, 150, 150, 'users/thumbnail/');
            $user->image = $url;
            $user->profile_pic = $thumbnailUrl;
        }
        $user->save();
        return $this->sendResponse($user, 'User profile update successfully', 202);
    }

    public function createThumbnail($file, $width, $height, $path)
    {
        $image = Image::read($file)->resize($width, $height, function ($constraint) {
            $constraint->aspectRatio();
        });
        $randomHash = Str::random(40);
        $thumbnailFilename = $randomHash . '.' . $file->getClientOriginalExtension();
        $destinationPathThumbnail = $path . $thumbnailFilename;
        Storage::disk('public')->put($destinationPathThumbnail, (string) $image->encode());
        $thumbnailUrl = Storage::disk('public')->url($destinationPathThumbnail);
        return $thumbnailUrl;
    }
}
