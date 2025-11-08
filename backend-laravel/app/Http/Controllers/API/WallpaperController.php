<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\API\BaseController;
use App\Models\Wallpaper;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Facades\Storage;

class WallpaperController extends BaseController
{
    public function __construct() {}
    /**
     * Display a listing of the resource.
     */
    public function index(Request $request)
    {
        $perPage = (int) $request->get('per_page', config('constants.RECORDS_PER_PAGE'));
        $currentPage = (int) $request->get('page', 1);
        $data = Wallpaper::orderBy('updated_at', 'desc')->paginate($perPage, ['*'], 'page', $currentPage);
        return $this->sendResponseWithPages($data, "Wallpaper fetched", 200);
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
                'name' => 'required',
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
        $wallpaper = new Wallpaper();
        $wallpaper->name = !empty($request->name) ? $request->name : '';
        if ($request->hasFile('image')) {
            $file = $request->file('image'); // Use $request->file() to get the file
            $path = $file->store('wallpapers', 'public'); // Store in 'public/audio'
            $url = Storage::disk('public')->url($path);
            $wallpaper->url =  $url ?? NULL;
        }
        $wallpaper->save();
        return $this->sendResponse($wallpaper, 'Wallpaper added successfully', 201);
    }

    /**
     * Display the specified resource.
     */
    public function show($id)
    {
        $wallpaper = Wallpaper::find($id);
        if (is_null($wallpaper)) {
            return   $this->sendError('Wallpaper not found', [], 404);
        }
        return $this->sendResponse($wallpaper, 'Wallpaper fetched', 200);
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
                'name' => 'required',
            ],
        );
        if ($validator->fails()) {
            return $this->sendError($validator->errors()->first(), ['error' => $validator->errors()->first()], 400);
        }
        $wallpaper =  Wallpaper::find($id);
        if (is_null($wallpaper)) {
            return   $this->sendError('Wallpaper not found', [], 404);
        }
        $wallpaper->name = !empty($request->name) ? $request->name : $wallpaper->name;
        if ($request->hasFile('image')) {
            $file = $request->file('image'); // Use $request->file() to get the file
            $path = $file->store('wallpapers', 'public'); // Store in 'public/audio'
            $url = Storage::disk('public')->url($path);
            $wallpaper->url =  $url ?? NULL;
        }
        $wallpaper->save();
        return $this->sendResponse($wallpaper, 'Wallpaper update successfully', 202);
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy($id)
    {
        $wallpaper = Wallpaper::find($id);
        if (is_null($wallpaper)) {
            return   $this->sendError('Wallpaper not found', [], 404);
        }
        $wallpaper->delete();
        return $this->sendResponse(null, 'Wallpaper deleted successfully', 204);
    }
}
