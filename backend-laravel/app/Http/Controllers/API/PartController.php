<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\API\BaseController;
use Illuminate\Http\Request;
use App\Models\Part;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Facades\DB;

class PartController extends BaseController
{
    protected $firebaseNotificationService;
    // public function __construct(FirebaseNotification $firebaseNotificationService)
    // {
    //     $this->firebaseNotificationService = $firebaseNotificationService;
    // }
    /**
     * Display a listing of the resource.
     */
    public function index(Request $request)
    {
        $perPage = (int) $request->get('per_page', config('constants.RECORDS_PER_PAGE'));
        $currentPage = (int) $request->get('page', 1);
        $data = Part::paginate($perPage, ['*'], 'page', $currentPage);
        return $this->sendResponseWithPages($data, "Picture section fetched", 200);
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        //
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        //FIXME to create auth

        // $validator = Validator::make($request->all(), [
        //     'title' => 'required',
        //     'type' => 'required|in:bhajan,ringtone',
        //     'duration' => 'required',
        //     // 'audio' => 'required|max:40480|mimes:mp3',
        // ]);
        // if ($validator->fails()) {
        //     return $this->sendError($validator->errors()->first(), ['error' => $validator->errors()->first()], 400);
        // }
        // $audio = new Audio();
        // $audio->title = !empty($request->title) ? $request->title : '';
        // $audio->type =  !empty($request->type) ? $request->type : '';
        // $audio->duration = !empty($request->duration) ? $request->duration : '';
        // if ($request->hasFile('audio')) {
        //     $file = $request->file('audio'); // Use $request->file() to get the file
        //     $path = $file->store('audios', 'public'); // Store in 'public/audio'
        //     $url = Storage::disk('public')->url($path);
        //     $audio->path =  $url ?? NULL;
        // }
        // $audio->save();
        // $tokens = User::whereNotNull('device_token')->where('device_token', '!=', '')->pluck('device_token')->toArray();
        // $title = "New Ringtone uploaded";
        // $body = "New Ringtone uploaded";
        // $data = [];
        // if ($request->type == "bhajan") {
        //     $title = "New bhajan uploaded";
        //     $body = "New bhajan uploaded";
        // }
        // if (!is_null($tokens)) {
        //     $this->firebaseNotificationService->sendToMultipleDevices($tokens, $title, $body, $data);
        // }
        // return $this->sendResponse($audio, 'Audio added successfully', 201);
    }

    /**
     * Display the specified resource.
     */
    public function show($id)
    {
        // $audio = Audio::find($id);
        // if (is_null($audio)) {
        //     return   $this->sendError('Audio not found', [], 404);
        // }
        // return $this->sendResponse($audio, 'Audio fetched', 200);
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(Request $product)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, $id)
    {
        // $validator = Validator::make($request->all(), [
        //     'title' => 'required',
        //     'type' => 'required|in:bhajan,ringtone',
        //     'duration' => 'required',
        //     'audio' => 'required|max:40480|mimes:mp3',
        // ]);
        // if ($validator->fails()) {
        //     return $this->sendError($validator->errors()->first(), ['error' => $validator->errors()->first()], 400);
        // }
        // $audio = Audio::find($id);
        // if (is_null($audio)) {
        //     return   $this->sendError('Audio not found', [], 404);
        // }
        // $audio->title = !empty($request->title) ? $request->title :   $audio->title;
        // $audio->type =  !empty($request->type) ? $request->type : $audio->type;
        // $audio->duration = !empty($request->duration) ? $request->duration : $audio->duration;
        // if ($request->hasFile('audio')) {
        //     $file = $request->file('audio'); // Use $request->file() to get the file
        //     $path = $file->store('audios', 'public'); // Store in 'public/audio'
        //     $url = Storage::disk('public')->url($path);
        //     $audio->path =  $url ?? NULL;
        // }
        // $audio->save();
        // $tokens = User::whereNotNull('device_token')->where('device_token', '!=', '')->pluck('device_token')->toArray();
        // $title = "Ringtone updated";
        // $body = "Ringtone updated";
        // $data = [];
        // if ($request->type == "bhajan") {
        //     $title = "Bhajan updated";
        //     $body = "Bhajan updated";
        // }
        // if (!is_null($tokens)) {
        //     $this->firebaseNotificationService->sendToMultipleDevices($tokens, $title, $body);
        // }
        // return $this->sendResponse($audio, 'Audio update successfully', 202);
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy($id)
    {
        // $audio = Audio::find($id);
        // if (is_null($audio)) {
        //     return   $this->sendError('Audio not found', [], 404);
        // }
        // $audio->delete();
        // return $this->sendResponse(null, 'Audio deleted successfully', 204);
    }
}
