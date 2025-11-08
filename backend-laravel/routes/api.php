<?php

use App\Http\Controllers\API\AartiController;
use App\Http\Controllers\API\AudioController;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\API\AuthController;
use App\Http\Controllers\API\GeetaPartsController;
use App\Http\Controllers\API\PartController;
use App\Http\Controllers\API\PictureController;
use App\Http\Controllers\API\UserController;
use App\Http\Controllers\API\VideoController;
use App\Http\Controllers\API\WallpaperController;
use App\Http\Controllers\API\PostController;

Route::controller(AuthController::class)->group(function () {
    Route::post('register', 'register');
    Route::post('login', 'login');
    Route::post('login/mobile', 'mobileLogin');
    Route::post('login/mobile/verify-otp', 'mobileOTPVerification');
    Route::post('forget-password', 'forgetPassword');
});
Route::get('/user', function (Request $request) {
    return $request->user();
})->middleware('auth:sanctum');


Route::middleware('auth:sanctum')->group(function () {
    Route::post('logout', [AuthController::class, 'logout']);
    Route::post('/block-user', [AuthController::class, 'blockUser']);
});

Route::controller(GeetaPartsController::class)->group(function () {
    Route::get('/geeta-parts', 'index'); // Read (all)
    Route::get('/geeta-parts/{id}', 'show'); // Read (specific)
    Route::middleware('auth:sanctum')->group(function () {
        Route::post('/geeta-parts', 'store'); // Create
        Route::put('/geeta-parts/{id}', 'update'); // Update
        Route::delete('/geeta-parts/{id}', 'destroy'); // Delete
    });
});

Route::controller(AudioController::class)->group(function () {
    Route::get('/audios', 'index'); // Read (all)
    Route::get('/audios/{id}', 'show'); // Read (specific)
    Route::middleware('auth:sanctum')->group(function () {
        Route::post('/audios', 'store'); // Create
        Route::put('/audios/{id}', 'update'); // Update
        Route::delete('/audios/{id}', 'destroy'); // Delete
    });
});

Route::controller(AartiController::class)->group(function () {
    Route::get('/aarti', 'index'); // Read (all)
    Route::get('/aarti/{id}', 'show'); // Read (specific)
    Route::middleware('auth:sanctum')->group(function () {
        Route::post('/aarti', 'store'); // Create
        Route::put('/aarti/{id}', 'update'); // Update
        Route::delete('/aarti/{id}', 'destroy'); // Delete
    });
});

Route::controller(VideoController::class)->group(function () {
    Route::get('/videos', 'index'); // Read (all)
    Route::get('/videos/{id}', 'show'); // Read (specific)
    Route::middleware('auth:sanctum')->group(function () {
        Route::post('/videos', 'store'); // Create
        Route::put('/videos/{id}', 'update'); // Update
        Route::delete('/videos/{id}', 'destroy'); // Delete
    });
});

Route::controller(WallpaperController::class)->group(function () {
    Route::get('/wallpapers', 'index'); // Read (all)
    Route::get('/wallpapers/{id}', 'show'); // Read (specific)
    Route::middleware('auth:sanctum')->group(function () {
        Route::post('/wallpapers', 'store'); // Create
        Route::put('/wallpapers/{id}', 'update'); // Update
        Route::delete('/wallpapers/{id}', 'destroy'); // Delete
    });
});

Route::controller(UserController::class)->group(function () {
    Route::middleware('auth:sanctum')->group(function () {
        Route::post('/update-profile', 'updateProfile');
        // Route::put('/wallpapers/{id}', 'update'); // Update
        // Route::delete('/wallpapers/{id}', 'destroy'); // Delete
    });
});

Route::controller(PartController::class)->group(function () {
    Route::get('/parts', 'index'); // Read (all)
    // Route::get('/parts/{id}', 'show'); // Read (specific)
    Route::middleware('auth:sanctum')->group(function () {
        // Route::post('/parts', 'store'); // Create
        // Route::put('/parts/{id}', 'update'); // Update
        // Route::delete('/parts/{id}', 'destroy'); // Delete
    });
});

Route::controller(PictureController::class)->group(function () {
    Route::get('/parts/pictures', 'index'); // Read (all)
    Route::get('/parts/{id}/pictures', 'getPictureByPart'); //Read By Part
    // Route::get('/parts/{id}', 'show'); // Read (specific)
    Route::middleware('auth:sanctum')->group(function () {
        Route::post('/parts/pictures', 'store'); // Create
        Route::put('/parts/pictures/{id}', 'update'); // Update
        Route::delete('/parts/pictures/{id}', 'destroy'); // Delete
    });
});

Route::controller(PostController::class)->group(function () {
    Route::get('/posts', 'index'); // Read (all)
    Route::get('/posts/amrit', 'amrit');
    Route::get('/posts/gallery', 'gallery');
    Route::get('/posts/{id}', 'show'); // Read (specific)
    Route::get('/posts/comments/{id}', 'allComment');
    Route::middleware('auth:sanctum')->group(function () {
        Route::post('/posts', 'store'); // Create
        Route::put('/posts/{id}', 'update'); // Update
        Route::delete('/posts/{id}', 'destroy'); // Delete
        //likes && dislikes
        Route::post('/posts/{id}/likes', 'postLike');
        Route::delete('/posts/{id}/likes', 'removeLike');
        Route::get('/posts/{id}/likes', 'allLike');
        //comment && remove comment

        Route::post('/posts/comments', 'addComment');
        Route::put('/posts/comments/{id}', 'updateComment');
        Route::delete('/posts/comments/{id}', 'removeComment');
        //Reply && remove Reply
        Route::post('/posts/replies', 'addReply');
        Route::put('/posts/replies/{id}', 'updateReply');
        Route::delete('/posts/replies/{id}', 'removeReply');
        //comment likes && remove comment like
        Route::post('/comments/likes', 'commentLike');
        Route::delete('/comments/likes/{post_id}/{comment_id}', 'removeCommentLike');
    });
});
