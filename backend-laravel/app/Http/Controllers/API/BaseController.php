<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Database\Eloquent\Model;
use stdClass;
use Illuminate\Pagination\LengthAwarePaginator;

class BaseController extends Controller
{
    /**
     * success response method.
     *
     * @return \Illuminate\Http\Response
     */
    public function sendResponse(array|stdClass|Model|null $data, string $message, int $status_code = 200)
    {
        $response = [
            'status' => true,
            'status_code' => $status_code,
            'message' => $message,
            'data'    => $data,
        ];


        return response()->json($response, 200);
    }
    /**
     * return error response.
     *
     * @return \Illuminate\Http\Response
     */
    public function sendError(string $error, array  $errorMessages = [], int $status_code = 404)
    {
        $response = [
            'status' => false,
            'status_code' => $status_code,
            'message' => $error,
        ];
        if (!empty($errorMessages)) {
            $response['data'] = $errorMessages;
        } else {
            $response['data'] = null;
        }
        return response()->json($response, $status_code);
    }
    public function sendResponseWithPages(array|stdClass|Model|null|LengthAwarePaginator $data, string $message, int $status_code = 200)
    {
        $response = [
            'status' => true,
            'status_code' => $status_code,
            'message' => $message,
            'data'    => $data->items(),
            'total' => $data->total(),
            'per_page' => $data->perPage(),
            'last_page' => $data->lastPage(),
            'current_page' =>  $data->currentPage(),
        ];
        return response()->json($response, 200);
    }
}
