<?php

namespace App\Http\Controllers\API;

use App\Constant\VideoLink;
use App\Http\Controllers\API\BaseController;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Validator;
use App\Models\Video;
use App\Services\FirebaseNotification;
use App\Models\User;
use Illuminate\Validation\Rules\Enum;

class VideoController extends BaseController
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
        $data = Video::orderBy('updated_at', 'desc')->paginate($perPage, ['*'], 'page', $currentPage);
        return $this->sendResponseWithPages($data, "Videos fetched", 200);
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
        $validator = Validator::make($request->all(), [
            'title' => 'required',
            'url' => 'required',
            // 'type' => ['required', 'in:youtube'],
        ]);
        if ($validator->fails()) {
            return $this->sendError($validator->errors()->first(), ['error' => $validator->errors()->first()], 400);
        }
        $video = new Video();
        $video->title = !empty($request->title) ? $request->title : '';
        $video->type = VideoLink::YOUTUBE;
        $video->url = !empty($request->url) ?
            getYouTubeLink($request->url)
            : 'https://www.youtube.com/watch?v=230JFm4IkhY';
        $video->save();
        $title = "New video uploaded.";
        $body = "Tap to watch videos of Jagat Swami Shri Krishna.";
        $data = [
            'title' => $title,
            'message' => $video->title,
            'image' => $video->url,
            'id' => $video->id,
            'type' => 'video'
        ];
        $this->firebaseNotificationService->broadcastNotification($title, $body, $data);
        return $this->sendResponse($video, 'Video added successfully', 201);
    }

    /**
     * Display the specified resource.
     */
    public function show($id)
    {
        $video = Video::find($id);
        if (is_null($video)) {
            return   $this->sendError('Video not found', [], 404);
        }
        return $this->sendResponse($video, 'Video fetched', 200);
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
        $video = Video::find($id);
        if (is_null($video)) {
            return   $this->sendError('Video not found', [], 404);
        }
        $video->title = !empty($request->title) ? $request->title :   $video->title;
        $video->url = !empty($request->url) ? $request->url :  $video->url;
        $video->save();
        $title = "Video uploaded.";
        $body = "Tap to watch videos of Jagat Swami Shri Krishna.";
        $data = [
            'title' => $title,
            'message' => $video->title,
            'image' => $video->url,
            'id' => $video->id,
            'type' => 'video'
        ];
        $this->firebaseNotificationService->broadcastNotification($title, $body, $data);
        return $this->sendResponse($video, 'Video update successfully', 202);
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy($id)
    {
        $video = Video::find($id);
        if (is_null($video)) {
            return   $this->sendError('Video not found', [], 404);
        }
        $video->delete();
        return $this->sendResponse(null, 'Video deleted successfully', 204);
    }
}
