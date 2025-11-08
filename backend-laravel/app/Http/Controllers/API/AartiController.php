<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\API\BaseController;
use Illuminate\Http\Request;
use App\Models\Aarti;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Validator;

class AartiController extends BaseController
{
    public function __construct() {}
    /**
     * Display a listing of the resource.
     */
    public function index(Request $request)
    {
        $perPage = (int) $request->get('per_page', config('constants.RECORDS_PER_PAGE'));
        $currentPage = (int) $request->get('page', 1);
        $data = Aarti::orderBy('updated_at', 'desc')->paginate($perPage, ['*'], 'page', $currentPage);
        return $this->sendResponseWithPages($data, "Aarti parts fetched", 200);
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
        $aarti = new Aarti();
        $aarti->title = !empty($request->title) ? $request->title : '';
        $aarti->content = !empty($request->content) ? $request->content : '';
        $aarti->save();
        return $this->sendResponse($aarti, 'Aarti added successfully', 201);
    }

    /**
     * Display the specified resource.
     */
    public function show($id)
    {
        $aarti = Aarti::find($id);
        if (is_null($aarti)) {
            return   $this->sendError('Aarti not found', [], 404);
        }
        return $this->sendResponse($aarti, 'Aarti fetched', 200);
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
        $aarti = Aarti::find($id);
        if (is_null($aarti)) {
            return   $this->sendError('Aarti not found', [], 404);
        }
        $aarti->title = !empty($request->title) ? $request->title :   $aarti->title;
        $aarti->content = !empty($request->content) ? $request->content :  $aarti->content;
        $aarti->save();
        return $this->sendResponse($aarti, 'Aarti update successfully', 202);
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy($id)
    {
        $aarti = Aarti::find($id);
        if (is_null($aarti)) {
            return   $this->sendError('Geeta not found', [], 404);
        }
        $aarti->delete();
        return $this->sendResponse(null, 'Geeta deleted successfully', 204);
    }
}
