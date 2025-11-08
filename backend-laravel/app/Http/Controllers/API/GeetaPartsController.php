<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Http\Controllers\API\BaseController;
use App\Models\GeetaParts;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Validator;

class GeetaPartsController extends BaseController
{
    public function __construct() {}
    /**
     * Display a listing of the resource.
     */
    public function index(Request $request)
    {
        $perPage = (int) $request->get('per_page', config('constants.RECORDS_PER_PAGE'));
        $currentPage = (int) $request->get('page', 1);
        $data = GeetaParts::orderBy('updated_at', 'desc')->paginate($perPage, ['*'], 'page', $currentPage);
        return $this->sendResponseWithPages($data, "Geeta parts fetched", 200);
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
            'content' => 'required',
        ]);
        if ($validator->fails()) {
            return $this->sendError($validator->errors()->first(), ['error' => $validator->errors()->first()], 400);
        }
        $geetaPart = new GeetaParts();
        $geetaPart->title = !empty($request->title) ? $request->title : '';
        $geetaPart->content = !empty($request->content) ? $request->content : '';
        $geetaPart->save();
        return $this->sendResponse($geetaPart, 'Geeta added successfully', 201);
    }

    /**
     * Display the specified resource.
     */
    public function show($id)
    {
        $geeta = GeetaParts::find($id);
        if (is_null($geeta)) {
            return   $this->sendError('Geeta not found', [], 404);
        }
        return $this->sendResponse($geeta, 'Geeta fetched', 200);
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
        $geetaPart = GeetaParts::find($id);
        if (is_null($geetaPart)) {
            return   $this->sendError('Geeta not found', [], 404);
        }
        $geetaPart->title = !empty($request->title) ? $request->title :   $geetaPart->title;
        $geetaPart->content = !empty($request->content) ? $request->content :  $geetaPart->content;
        $geetaPart->save();
        return $this->sendResponse($geetaPart, 'Geeta update successfully', 202);
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy($id)
    {
        $geetaPart = GeetaParts::find($id);
        if (is_null($geetaPart)) {
            return   $this->sendError('Geeta not found', [], 404);
        }
        $geetaPart->delete();
        return $this->sendResponse(null, 'Geeta deleted successfully', 204);
    }
}
