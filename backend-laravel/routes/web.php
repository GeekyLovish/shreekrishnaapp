<?php

use App\Http\Controllers\HomeController;
use Illuminate\Support\Facades\Route;
use Illuminate\Support\Facades\Artisan;
use Illuminate\Support\Facades\Auth;
use Illuminate\Foundation\Auth\EmailVerificationRequest;
use Illuminate\Http\Request;
use App\Http\Controllers\Auth\VerificationController;
use App\Models\User;

Route::get('/', function () {
    return view('welcome');
})->name('home');
Auth::routes();
Route::get('/email/verify', function () {
    return view('auth.verify');
})->name('verification.notice');

Route::get('/email/verify/{id}/{hash}', [VerificationController::class, 'verify'])
    ->middleware(['signed', 'throttle:6,1'])->name('verification.verify');

Route::post('/email/verification-notification', [VerificationController::class, 'resend'])
    ->middleware(['throttle:6,1'])
    ->name('verification.resend');


// Route::get('/email/verify/{id}/{hash}', [VerificationController::class, 'verify'])
//     ->middleware(['signed', 'throttle:6,1'])
//     ->name('verification.verify');



Route::get('/delete-account', function () {
    return view('delete-account');
})->name('delete-account');
Route::get('/account/delete', [HomeController::class, 'index'])->name('account.delete');
Route::post('/account/delete', [HomeController::class, 'deleteRequest'])->name('account.delete.request');
Route::post('/account/delete/{id}/confirm', [HomeController::class, 'confirmDelete'])->name('account.delete.confirm');
Route::get('/migrate', function () {
    $exitCode = Artisan::call('migrate', [
        '--path' => 'database/migrations/2025_09_15_155557_add_image_to_comments_table.php',
    ]);
    return [
        '--path' => 'database/migrations/2025_09_15_155557_add_image_to_comments_table.php',
    ];
});
Route::get('/optimize', function () {
    $exitCode = Artisan::call('optimize');
    return $exitCode;
});
Route::get('/db-seed', function () {
    $exitCode = Artisan::call('db:seed');
    return $exitCode;
});
Route::get('/clear-config', function () {
    $exitCode = Artisan::call('config:clear');
    return $exitCode;
});
Route::get('/optimize-config', function () {
    $exitCode = Artisan::call('optimize:clear');
    return $exitCode;
});
Route::get('/link-storage', function () {
    $exitCode = Artisan::call('storage:link');
    return $exitCode;
});
