<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\API\BaseController;
use App\Models\Part;
use App\Models\Pictures;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Storage;
use App\Models\User;
use App\Services\FirebaseNotification;

class PictureController extends BaseController
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
        $data = Pictures::orderBy('updated_at', 'desc')->paginate($perPage, ['*'], 'page', $currentPage);
        return $this->sendResponseWithPages($data, "Pictures fetched", 200);
    }

    public function getPictureByPart(Request $request, $id)
    {
        $perPage = (int) $request->get('per_page', config('constants.RECORDS_PER_PAGE'));
        $currentPage = (int) $request->get('page', 1);
        $data = Pictures::where('part_id', $id)->paginate($perPage, ['*'], 'page', $currentPage);
        return $this->sendResponseWithPages($data, "Pictures fetched", 200);
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
        $validator = Validator::make(
            $request->all(),
            [
                'part_id' => 'required',
                'image' => 'required|image|mimes:jpeg,png,jpg,gif,svg|max:2048',
            ],
            [
                'image.required' => 'An image is required.',
                'image.image' => 'The file must be an image.',
                'image.mimes' => 'Only jpeg, png, jpg, gif, and svg images are allowed.',
                'image.max' => 'The image may not be greater than 2MB.',
            ]
        );
        if ($validator->fails()) {
            return $this->sendError($validator->errors()->first(), ['error' => $validator->errors()->first()], 400);
        }
        $part = Part::where('id', $request->part_id)->get()->first();
        if (is_null($part)) {
            return   $this->sendError('Part not found', [], 404);
        }
        $picture = new Pictures();
        $picture->part_id = $part->id;
        $url = NULL;
        if ($request->hasFile('image')) {
            $file = $request->file('image'); // Use $request->file() to get the file
            $path = $file->store('pictures', 'public'); // Store in 'public/audio'
            $url = Storage::disk('public')->url($path);
            $picture->url =  $url ?? NULL;
        }
        $picture->save();
        $title = "Uploaded a new picture.";
        $body = "Tap to see pictures of Lord Shri Krishna.";
        $data = [
            'title' => $title,
            'message' => $body,
            'image' => $url,
            'id' => $picture->id,
            'type' => 'picture',
            'subType' => 'picture',
        ];
        $this->firebaseNotificationService->broadcastNotification($title, $body, $data);
        return $this->sendResponse($picture, 'Picture added successfully', 201);
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
        $validator = Validator::make(
            $request->all(),
            [
                'part_id' => 'required',
            ]
        );
        if ($validator->fails()) {
            return $this->sendError($validator->errors()->first(), ['error' => $validator->errors()->first()], 400);
        }
        $part = Part::where('id', $request->part_id)->get()->first();
        if (is_null($part)) {
            return   $this->sendError('Part not found', [], 404);
        }
        $picture = Pictures::find($id);
        if (is_null($picture)) {
            return   $this->sendError('Picture not found', [], 404);
        }
        $picture->part_id = $part->id;
        $url = $picture->url;
        if ($request->hasFile('image')) {
            $file = $request->file('image'); // Use $request->file() to get the file
            $path = $file->store('pictures', 'public'); // Store in 'public/audio'
            $url = Storage::disk('public')->url($path);
            $picture->url =  $url ?? NULL;
        }
        $picture->save();
        $title = "Picture uploaded.";
        $body = "Tap to see pictures of Lord Shri Krishna.";
        $data = [
            'title' => $title,
            'message' => $body,
            'image' => $url,
            'id' => $picture->id,
            'type' => 'picture',
            'subType' => 'picture',
        ];
        $this->firebaseNotificationService->broadcastNotification($title, $body, $data);
        return $this->sendResponse($picture, 'Picture update successfully', 202);
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy($id)
    {
        $picture = Pictures::find($id);
        if (is_null($picture)) {
            return   $this->sendError('Picture not found', [], 404);
        }
        $picture->delete();
        return $this->sendResponse(null, 'Picture deleted successfully', 204);
    }
}
