<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\API\BaseController;
use Illuminate\Http\Request;
use App\Models\Audio;
use App\Models\User;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Facades\Storage;
use App\Services\FirebaseNotification;
use App\Constant\AudioType;

class AudioController extends BaseController
{
    protected $firebaseNotificationService;
    public function __construct(FirebaseNotification $firebaseNotificationService)
    {
        $this->firebaseNotificationService = $firebaseNotificationService;
    }
    /**
     * Display a listing of the resource.
     */
    public function index(Request $request)
    {
        $perPage = (int) $request->get('per_page', config('constants.RECORDS_PER_PAGE'));
        $currentPage = (int) $request->get('page', 1);
        $type = $request->get('type');
        $data = Audio::orderBy('updated_at', 'desc');  // Start the query
        if (!is_null($type)) {
            $data->where('type', $type);  // Only add the 'type' filter if $type is not null
        }
        $data = $data->paginate($perPage, ['*'], 'page', $currentPage);
        return $this->sendResponseWithPages($data, "Audio parts fetched", 200);
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
        $validator = Validator::make($request->all(), [
            'title' => 'required',
            'type' => 'required|in:bhajan,ringtone',
            'duration' => 'required',
            // 'audio' => 'required|max:40480|mimes:mp3',
        ]);
        if ($validator->fails()) {
            return $this->sendError($validator->errors()->first(), ['error' => $validator->errors()->first()], 400);
        }
        $audio = new Audio();
        $audio->title = !empty($request->title) ? $request->title : '';
        $audio->type =  !empty($request->type) ? $request->type : '';
        $audio->duration = !empty($request->duration) ? $request->duration : '';
        $url = NULL;
        if ($request->hasFile('audio')) {
            $file = $request->file('audio'); // Use $request->file() to get the file
            $path = $file->store('audios', 'public'); // Store in 'public/audio'
            $url = Storage::disk('public')->url($path);
            $audio->path =  $url ?? NULL;
        }
        $audio->save();
        $title = "New ringtone uploaded.";
        $body = $audio->title;
        $data = [
            'title' => $title,
            'message' => $body,
            'image' => $url,
            'id' => $audio->id,
            'type' => AudioType::RINGTONE,
            'subType' => AudioType::RINGTONE,
        ];
        if ($request->type == AudioType::BHAJAN) {
            $title = "New bhajan uploaded.";
            $data = [
                'title' => $title,
                'message' => $body,
                'image' => $url,
                'id' => $audio->id,
                'type' => AudioType::BHAJAN,
                'subType' => AudioType::BHAJAN,
            ];
        }
        // $this->firebaseNotificationService->broadcastNotification($title, $body, $data);
        return $this->sendResponse($audio, 'Audio added successfully', 201);
    }

    /**
     * Display the specified resource.
     */
    public function show($id)
    {
        $audio = Audio::find($id);
        if (is_null($audio)) {
            return   $this->sendError('Audio not found', [], 404);
        }
        return $this->sendResponse($audio, 'Audio fetched', 200);
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
        $audio = Audio::find($id);
        if (is_null($audio)) {
            return   $this->sendError('Audio not found', [], 404);
        }
        $audio->title = !empty($request->title) ? $request->title :   $audio->title;
        $audio->type =  !empty($request->type) ? $request->type : $audio->type;
        $audio->duration = !empty($request->duration) ? $request->duration : $audio->duration;
        $url =  $audio->path;
        if ($request->hasFile('audio')) {
            $file = $request->file('audio'); // Use $request->file() to get the file
            $path = $file->store('audios', 'public'); // Store in 'public/audio'
            $url = Storage::disk('public')->url($path);
            $audio->path =  $url ?? NULL;
        }
        $audio->save();
        $title = "Ringtone uploaded.";
        $body = $audio->title;
        $data = [
            'title' => $title,
            'message' => $body,
            'image' => $url,
            'id' => $audio->id,
            'type' => AudioType::RINGTONE,
            'subType' => AudioType::RINGTONE,
        ];
        if ($request->type == AudioType::BHAJAN) {
            $title = "Bhajan uploaded.";
            $data = [
                'title' => $title,
                'message' => $body,
                'image' => $url,
                'id' => $audio->id,
                'type' => AudioType::BHAJAN,
                'subType' => AudioType::BHAJAN,
            ];
        }
        // $this->firebaseNotificationService->broadcastNotification($title, $body, $data);
        return $this->sendResponse($audio, 'Audio update successfully', 202);
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy($id)
    {
        $audio = Audio::find($id);
        if (is_null($audio)) {
            return   $this->sendError('Audio not found', [], 404);
        }
        $audio->delete();
        return $this->sendResponse(null, 'Audio deleted successfully', 204);
    }
}
