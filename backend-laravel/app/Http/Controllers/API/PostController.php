<?php

namespace App\Http\Controllers\API;

use App\Constant\PostType;
use Illuminate\Http\Request;
use App\Http\Controllers\API\BaseController;
use App\Models\Comment;
use App\Models\CommentLike;
use App\Services\FirebaseNotification;
use App\Models\Post;
use App\Models\PostLike;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Storage;
use App\Models\User;
use Illuminate\Support\Facades\Auth;

use function Laravel\Prompts\table;

class PostController extends BaseController
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
        $data = Post::orderBy('updated_at', 'desc')->paginate($perPage, ['*'], 'page', $currentPage);
        return $this->sendResponseWithPages($data, "Post section fetched", 200);
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
        $user_id = Auth::user()->id;

        $validator = Validator::make(
            $request->all(),
            [
                'title' => 'required',
                'type' => 'required|in:gallery,amrit',
                'content' => 'required',
                'link_type' => 'required|in:page,learn,web,watch',
                'link' => 'required',
                'image' => 'required|image|mimes:jpeg,png,jpg,gif,svg',
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
        $post = new Post();
        $post->user_id  = $user_id;
        $post->title = !empty($request->title) ? $request->title : '';
        $post->type =  !empty($request->type) ? $request->type : '';
        $post_connect = !empty($request->content) ? $request->content :  '';
        $post_connect = removeUTFCharacters($post_connect);
        $post->content =  $post_connect;
        $post->link_type = !empty($request->link_type) ? $request->link_type : '';
        $post->link = !empty($request->link) ? $request->link : '';
        $url = NULL;
        if ($request->hasFile('image')) {
            $file = $request->file('image'); // Use $request->file() to get the file
            $path = $file->store('posts', 'public'); // Store in 'public/posts'
            $url = Storage::disk('public')->url($path);
            $post->url =  $url ?? NULL;
        }
        $post->save();
        $title = "New picture uploaded in gallery.";
        $body = cleanHTMLtags($post->content);
        $data = [
            'title' => $title,
            'message' => $post->content,
            'image' => $url,
            'id' => $post->id,
            'type' => PostType::GALLERY,
            'subType' => PostType::GALLERY,
            'sub_type' => PostType::GALLERY,
            'like_type' =>  "",
            'total_comments' => 0,
            'total_likes' => 0,
            'likes_types' =>  ""
        ];
        if ($request->type == PostType::AMRIT) {
            $title = "New picture uploaded in amrit.";
            $data = [
                'title' => $title,
                'message' =>  $body,
                'image' => $url,
                'id' => $post->id,
                'type' => PostType::AMRIT,
                'subType' => PostType::AMRIT,
                'sub_type' => PostType::AMRIT,
                'like_type' =>  "",
                'total_comments' => 0,
                'total_likes' => 0,
                'likes_types' =>  ""
            ];
        }
        $this->firebaseNotificationService->broadcastNotification($title, $body, $data);
        return $this->sendResponse($post, 'Post added successfully', 201);
    }

    /**
     * Display the specified resource.
     */
    public function show($id)
    {
        $post = Post::find($id);
        if (is_null($post)) {
            return   $this->sendError('Post not found', [], 404);
        }
        return $this->sendResponse($post, 'Post fetched', 200);
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
        $post = Post::find($id);
        if (is_null($post)) {
            return   $this->sendError('Post not found', [], 404);
        }
        $post->title = !empty($request->title) ? $request->title :   $post->title;
        $post->type =  !empty($request->type) ? $request->type : $post->type;
        $post_connect = !empty($request->content) ? $request->content :  '';
        $post_connect = removeUTFCharacters($post_connect);
        $post->content =  $post_connect;
        $post->link_type = !empty($request->link_type) ? $request->link_type : $post->link_type;
        $post->link = !empty($request->link) ? $request->link : $post->link;
        $url = $post->url;
        if ($request->hasFile('image')) {
            $file = $request->file('image'); // Use $request->file() to get the file
            $path = $file->store('posts', 'public'); // Store in 'public/posts'
            $url = Storage::disk('public')->url($path);
            $post->url =  $url ?? NULL;
        }
        $post->save();
        $totalComments = Comment::where(['post_id' => $post->id])->get()->count();
        $totalLikes = PostLike::where(['post_id' => $post->id])->get()->count();
        $likes_types = PostLike::where(['post_id' => $post->id])->get()->pluck('type')->toArray();
        $title = "Picture uploaded in gallery.";
        $body = cleanHTMLtags($post->content);
        $data = [
            'title' => $title,
            'message' => $body,
            'image' => $url,
            'id' => $post->id,
            'type' => PostType::GALLERY,
            'subType' => PostType::GALLERY,
            'sub_type' => PostType::GALLERY,
            'like_type' =>  "",
            'total_comments' => $totalComments,
            'total_likes' => $totalLikes,
            'likes_types' =>   implode(",", $likes_types)
        ];
        if ($request->type == PostType::AMRIT) {
            $title = "Picture uploaded in amrit.";
            $data = [
                'title' => $title,
                'message' => $body,
                'image' => $url,
                'id' => $post->id,
                'type' => PostType::AMRIT,
                'subType' => PostType::AMRIT,
                'sub_type' => PostType::AMRIT,
                'like_type' =>  "",
                'total_comments' => $totalComments,
                'total_likes' => $totalLikes,
                'likes_types' =>   implode(",", $likes_types)
            ];
        }
        $this->firebaseNotificationService->broadcastNotification($title, $body, $data);
        return $this->sendResponse($post, 'Post update successfully', 202);
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy($id)
    {
        $post = Post::find($id);
        if (is_null($post)) {
            return   $this->sendError('Post not found', [], 404);
        }
        $filePath = str_replace('https://app.ramankumarynr.com', '', $post->url);
        if (Storage::exists($filePath)) {
            Storage::delete();
        }
        $commentIDs = Comment::where('post_id', $id)->get()->select("id")->toArray();
        CommentLike::whereIn('comment_id', $commentIDs)->delete();
        Comment::whereIn('id', $commentIDs)
            ->whereNotNull('comment_id')
            ->delete();
        Comment::where("id", $commentIDs)->delete();
        PostLike::where("post_id", $id)->delete();
        $post->delete();
        return $this->sendResponse(null, 'Post deleted successfully', 204);
    }

    public function amrit(Request $request)
    {
        $user_id = 0;
        $is_blocked = 0;
        if ($request->user('sanctum')) {
            $user_id = auth('sanctum')->user()->id;
            $is_blocked = auth('sanctum')->user()->is_blocked;
        }
        $perPage = (int) $request->get('per_page', config('constants.RECORDS_PER_PAGE'));
        $currentPage = (int) $request->get('page', 1);
        $data = Post::where(['type' => PostType::AMRIT])->with('postedBy')->withCount('likes')->withCount('comments')->orderBy('updated_at', 'desc')->paginate($perPage, ['*'], 'page', $currentPage);
        $data->getCollection()->transform(function ($data) use ($user_id, $is_blocked) {
            $data->is_blocked = $is_blocked ? true : false;
            $data->liked_by_me = $data->likes->contains('user_id', $user_id);
            $data->liked_by_me_type = $data->likes->where('user_id', $user_id)->select('type')->first();
            $data->likes_types = $data->likes->pluck('type')->unique()->values()->all();
            $data->likes = $data->likes()->limit(4);
            unset($data->likes);
            return $data;
        });
        return $this->sendResponseWithPages($data, "Amrit section fetched", 200);
    }
    public function gallery(Request $request)
    {
        $user_id = 0;
        $is_blocked = 0;
        if ($request->user('sanctum')) {
            $user_id = auth('sanctum')->user()->id;
            $is_blocked = auth('sanctum')->user()->is_blocked;
        }
        $perPage = (int) $request->get('per_page', config('constants.RECORDS_PER_PAGE'));
        $currentPage = (int) $request->get('page', 1);
        $data = Post::where(['type' => PostType::GALLERY])->with('postedBy')->withCount('likes')->withCount('comments')->orderBy('updated_at', 'desc')->paginate($perPage, ['*'], 'page', $currentPage);
        $data->getCollection()->transform(function ($data) use ($user_id, $is_blocked) {
            $data->is_blocked = $is_blocked ? true : false;
            $data->liked_by_me = $data->likes->contains('user_id', $user_id);
            $data->liked_by_me_type = $data->likes->where('user_id', $user_id)->select('type')->first();
            $data->likes_types = $data->likes->pluck('type')->unique()->values()->all();
            $data->likes = $data->likes()->limit(4);
            unset($data->likes);
            return $data;
        });
        return $this->sendResponseWithPages($data, "Gallery fetched", 200);
    }
    public function postLike(Request $request, $id)
    {
        $user_id = Auth::user()->id;
        $post = Post::find($id);
        $validator = Validator::make($request->all(), [
            'type' => 'required|in:like,love',
        ]);
        if ($validator->fails()) {
            return $this->sendError($validator->errors()->first(), ['error' => $validator->errors()->first()], 400);
        }
        if (is_null($post)) {
            return   $this->sendError('Post not found', [], 404);
        }
        $isLikedBefore = PostLike::where(['user_id' => $user_id, 'post_id' => $post->id])->get()->first();
        if (is_null($isLikedBefore)) {
            $like = new PostLike();
            $like->user_id = $user_id;
            $like->post_id = $post->id;
            $like->type = $request->type;
            $like->save();

            $user_data = User::where(['id' => $user_id])->first();
            $totalComments = Comment::where(['post_id' => $post->id])->get()->count();
            $totalLikes = PostLike::where(['post_id' => $post->id])->get()->count();
            $likes_types = PostLike::where(['post_id' => $post->id])->get()->pluck('type')->toArray();
            $name = ($user_data->name) ? $user_data->name : 'User';
            $title =  $name . " liked the " . $post->type . ' post';
            $body = "";
            $multicast = [
                'title' => $title,
                'message' => $body,
                'image' => $post->url,
                'id' => $post->id,
                'type' => $post->type,
                'subType' => $post->type,
                'sub_type' => $post->type,
                'like_type' => $request->type ? $request->type : "",
                'total_comments' => $totalComments,
                'total_likes' => $totalLikes,
                'likes_types' =>  implode(",", $likes_types)
            ];
            $this->firebaseNotificationService->broadcastNotification($title, $body, $multicast);
            return $this->sendResponse($like, "Post Liked", 201);
        } else {
            $isLikedBefore->type = $request->type;
            $isLikedBefore->save();
            return $this->sendResponse($isLikedBefore, "Post Liked", 201);
        }
    }
    public function removeLike(Request $request, $id)
    {
        $user_id = Auth::user()->id;
        $post = Post::find($id);
        if (is_null($post)) {
            return   $this->sendError('Post not found', [], 404);
        }
        $isLikedBefore = PostLike::where(['user_id' => $user_id, 'post_id' => $post->id])->get()->first();
        if (is_null($isLikedBefore)) {
            return   $this->sendError('Like not found', [], 404);
        } else {
            $isLikedBefore->delete();
            return $this->sendResponse(null, "Liked removed", 201);
        }
    }
    public function addComment(Request $request)
    {
        $user_data = Auth::user();
        if ($user_data && $user_data->is_blocked == '1') {
            return  $this->sendError('You are blocked. So, You can\'t comment on this post', [], 403);
        }
        $validator = Validator::make($request->all(), [
            'comment' => 'required',
            'post_id' => 'required',
        ]);
        if ($validator->fails()) {
            return $this->sendError($validator->errors()->first(), ['error' => $validator->errors()->first()], 400);
        }
        $post = Post::find($request->post_id);
        if (is_null($post)) {
            return   $this->sendError('Post not found', [], 404);
        }
        $comment = new Comment();
        $comment->user_id = $user_data->id;
        $comment->post_id = $post->id;
        $comment->comment = $request->comment;
        $comment->comment_id = Null;
        if ($request->hasFile('image')) {
            $file = $request->file('image'); // Use $request->file() to get the file
            $path = $file->store('posts/comments/images', 'public'); // Store in 'public/posts'
            $url = Storage::disk('public')->url($path);
            $comment->image =  $url ?? NULL;
        }
        $comment->save();
        $tokens = User::where(['id' => $post->user_id])->whereNotNull('device_token')->where('device_token', '!=', '')->pluck('device_token')->toArray();
        $like_type = PostLike::where([
            'user_id' => $post->user_id,
            'post_id' => $post->id
        ])->get()->first();

        $totalComments = Comment::where(['post_id' => $post->id])->get()->count();
        $totalLikes = PostLike::where(['post_id' => $post->id])->get()->count();
        $likes_types = PostLike::where(['post_id' => $post->id])->get()->pluck('type')->toArray();
        $name = ($user_data->name) ? $user_data->name : 'User';
        $title =  $name . " commented on your post.";
        $body = 'Comment :: ' . $request->comment;
        $data = [
            'title' => $title,
            'message' => $body,
            'image' => $post->url,
            'id' => $post->id,
            'commentID' => $comment->id,
            'type' => $post->type,
            'subType' => 'comment',
            'sub_type' => 'comment',
            'like_type' => $like_type ? $like_type->type : "",
            'total_comments' => $totalComments,
            'total_likes' => $totalLikes,
            'likes_types' =>  implode(",", $likes_types)
        ];
        if (!is_null($tokens)) {
            $this->firebaseNotificationService->sendToMultipleDevices($tokens, $title, $body, $data);
        }
        $name = ($user_data->name) ? $user_data->name : 'User';
        $title =  $name . " commented on post.";
        $body = 'Comment :: ' . $request->comment;
        $multicast = [
            'title' => $title,
            'message' => $body,
            'image' => $post->url,
            'id' => $post->id,
            'commentID' => $comment->id,
            'type' => $post->type,
            'subType' => 'comment',
            'sub_type' => 'comment',
            'like_type' => $like_type ? $like_type->type : "",
            'total_comments' => $totalComments,
            'total_likes' => $totalLikes,
            'likes_types' =>  implode(",", $likes_types)
        ];
        $this->firebaseNotificationService->broadcastNotification($title, $body, $multicast);
        return $this->sendResponse($comment, "Comment posted", 201);
    }
    public function removeComment(Request $request, $id)
    {
        if (Auth::user()->hasRole('Administrator')) {
            $comment = Comment::where(['id' => $id])->get()->first();
            if (is_null($comment)) {
                return   $this->sendError('Comment not found', [], 404);
            }
            $replies = Comment::where('comment_id', $id)->pluck('id')->toArray();
            CommentLike::where('comment_id', $id)->delete();
            CommentLike::whereIn('comment_id', $replies)->delete();
            Comment::whereIn('id', $replies)
                ->whereNotNull('comment_id')
                ->delete();
            $comment->delete();
            return $this->sendResponse(null, 'Comment deleted successfully', 204);
        }
        $user_id = Auth::user()->id;
        $comment = Comment::where(['id' => $id, 'user_id' => $user_id])->get()->first();
        if (is_null($comment)) {
            return   $this->sendError('Comment not found', [], 404);
        }
        $comment->delete();
        return $this->sendResponse(null, 'Comment deleted successfully', 204);
    }
    public function updateComment(Request $request, $id)
    {
        $validator = Validator::make($request->all(), [
            'comment' => 'required',
        ]);
        if ($validator->fails()) {
            return $this->sendError($validator->errors()->first(), ['error' => $validator->errors()->first()], 400);
        }
        if (Auth::user()->hasRole('Administrator')) {
            $comment = Comment::where(['id' => $id])->get()->first();
            if (is_null($comment)) {
                return   $this->sendError('Comment not found', [], 404);
            }
            $url = $comment->image;
            if ($request->hasFile('image')) {
                $file = $request->file('image'); // Use $request->file() to get the file
                $path = $file->store('posts/comments/images', 'public'); // Store in 'public/posts'
                $url = Storage::disk('public')->url($path);
                $comment->image =  $url ?? NULL;
            }
            $comment->comment = $request->comment;
            $comment->save();
            return $this->sendResponse($comment, 'Comment update successfully', 202);
        }
        $user_id = Auth::user()->id;
        if (Auth::user()->is_blocked == '1') {
            return  $this->sendError('You are blocked. So, You can\'t comment on this post', [], 403);
        }
        $comment = Comment::where(['id' => $id, 'user_id' => $user_id])->get()->first();
        if (is_null($comment)) {
            return   $this->sendError('Comment not found', [], 404);
        }
        $url = $comment->image;
        if ($request->hasFile('image')) {
            $file = $request->file('image'); // Use $request->file() to get the file
            $path = $file->store('posts/comments/images', 'public'); // Store in 'public/posts'
            $url = Storage::disk('public')->url($path);
            $comment->image =  $url ?? NULL;
        }
        $comment->comment = $request->comment;
        $comment->save();
        return $this->sendResponse($comment, 'Comment update successfully', 202);
    }
    public function allComment(Request $request, $id)
    {
        $user_id = 0;
        if ($request->user('sanctum')) {
            $user_id = auth('sanctum')->user()->id;
        }
        $perPage = (int) $request->get('per_page', config('constants.RECORDS_PER_PAGE'));
        $currentPage = (int) $request->get('page', 1);
        $data = Comment::where(['post_id' => $id, 'is_parent' => 1])->with('commentedBy')->with('replies')->withCount('commentLikes')->paginate($perPage, ['*'], 'page', $currentPage);
        $data->getCollection()->transform(function ($data) use ($user_id) {
            $data->liked_by_me = $data->commentLikes->contains('user_id', $user_id);
            foreach ($data->replies as $reply) {
                $reply->liked_by_me = $reply->commentLikes->contains('user_id', $user_id);
                unset($reply->commentLikes); // Optionally, remove commentLikes if you don't need them in the response
            }
            unset($data->commentLikes);
            return $data;
        });
        return $this->sendResponseWithPages($data, "Comment fetched", 200);
    }
    public function allLike(Request $request, $id)
    {
        $perPage = (int) $request->get('per_page', config('constants.RECORDS_PER_PAGE'));
        $currentPage = (int) $request->get('page', 1);
        $data = PostLike::where('post_id', $id)->with('likedBy')->paginate($perPage, ['*'], 'page', $currentPage);
        return $this->sendResponseWithPages($data, "Post likes fetched", 200);
    }
    public function commentLike(Request $request)
    {
        $user_id = Auth::user()->id;
        $validator = Validator::make($request->all(), [
            'type' => 'required|in:like,love',
            'post_id' => 'required',
            'comment_id' => 'required',
        ]);
        if ($validator->fails()) {
            return $this->sendError($validator->errors()->first(), ['error' => $validator->errors()->first()], 400);
        }
        $post = Post::find($request->post_id);
        if (is_null($post)) {
            return   $this->sendError('Post not found', [], 404);
        }
        $comment = Comment::where(['id' => $request->comment_id, 'post_id' =>  $post->id])->get()->first();
        if (is_null($comment)) {
            return   $this->sendError('Comment not found', [], 404);
        }
        $isLikedBefore = CommentLike::where(['user_id' => $user_id, 'post_id' => $post->id, 'comment_id' => $comment->id])->get()->first();
        if (is_null($isLikedBefore)) {
            $like = new CommentLike();
            $like->comment_id = $comment->id;
            $like->user_id = $user_id;
            $like->post_id = $post->id;
            $like->type = $request->type;
            $like->save();
            return $this->sendResponse($like, "Comment liked", 201);
        } else {
            $isLikedBefore->type = $request->type;
            $isLikedBefore->save();
            return $this->sendResponse($isLikedBefore, "Comment liked", 201);
        }
    }
    public function removeCommentLike(Request $request, $post_id, $comment_id)
    {
        $user_id = Auth::user()->id;
        // $validator = Validator::make($request->all(), [
        //     'post_id' => 'required',
        //     'comment_id' => 'required',
        // ]);
        // if ($validator->fails()) {
        //     return $this->sendError($validator->errors()->first(), ['error' => $validator->errors()->first()], 400);
        // }
        $isLikedBefore = CommentLike::where(['user_id' => $user_id, 'post_id' => $post_id, 'comment_id' => $comment_id])->get()->first();
        if (is_null($isLikedBefore)) {
            return   $this->sendError('Comment like not found', [], 404);
        } else {
            $isLikedBefore->delete();
            return $this->sendResponse(null, "Comment liked removed", 201);
        }
    }

    public function addReply(Request $request)
    {
        $user_data = Auth::user();
        if ($user_data && $user_data->is_blocked == '1') {
            return  $this->sendError('You are blocked. So, You can\'t comment on this post', [], 403);
        }
        $validator = Validator::make($request->all(), [
            'comment' => 'required',
            'post_id' => 'required',
            'comment_id' => 'required',
        ]);
        if ($validator->fails()) {
            return $this->sendError($validator->errors()->first(), ['error' => $validator->errors()->first()], 400);
        }
        $post = Post::find($request->post_id);
        if (is_null($post)) {
            return   $this->sendError('Post not found', [], 404);
        }
        $lastComment = Comment::where(['id' => $request->comment_id, 'post_id' =>  $post->id])->get()->first();
        if (is_null($lastComment)) {
            return   $this->sendError('Comment not found', [], 404);
        }
        $comment = new Comment();
        $comment->user_id = $user_data->id;
        $comment->post_id = $post->id;
        $comment->comment = $request->comment;
        $comment->comment_id = $lastComment->id;
        $comment->is_parent = 0;
        if ($request->hasFile('image')) {
            $file = $request->file('image'); // Use $request->file() to get the file
            $path = $file->store('posts/comments/images', 'public'); // Store in 'public/posts'
            $url = Storage::disk('public')->url($path);
            $comment->image =  $url ?? NULL;
        }
        $comment->save();
        $like_type = PostLike::where([
            'user_id' => $lastComment->user_id,
            'post_id' => $post->id
        ])->get()->first();
        $tokens = User::where(['id' => $lastComment->user_id])->whereNotNull('device_token')->where('device_token', '!=', '')->pluck('device_token')->toArray();

        $totalComments = Comment::where(['post_id' => $post->id])->get()->count();
        $totalLikes = PostLike::where(['post_id' => $post->id])->get()->count();
        $likes_types = PostLike::where(['post_id' => $post->id])->get()->pluck('type')->toArray();
        $name = ($user_data->name) ? $user_data->name : 'User';
        $title =  $name . " replied to your comment.";
        $body = 'Reply :: ' . $request->comment;
        $data = [
            'title' => $title,
            'message' => $body,
            'image' => $post->url,
            'id' => $post->id,
            'commentID' => $lastComment->id,
            'replyID' => $comment->id,
            'type' => $post->type,
            'subType' => 'comment',
            'sub_type' => 'comment',
            'like_type' => $like_type ? $like_type->type : "",
            'total_comments' => $totalComments,
            'total_likes' => $totalLikes,
            'likes_types' =>  implode(",", $likes_types)
        ];
        if (!is_null($tokens)) {
            $this->firebaseNotificationService->sendToMultipleDevices($tokens, $title, $body, $data);
        }
        //Send notification to all users
        $second_user = User::where(['id' => $lastComment->user_id])->first();
        $name = ($user_data->name) ? $user_data->name : 'User';
        $second_name = ($second_user->name) ? $second_user->name : 'User';
        $title =  $name . " replied to " . $second_name . "'s comment.";
        $body = 'Reply :: ' . $request->comment;
        $multicast = [
            'title' => $title,
            'message' => $body,
            'image' => $post->url,
            'id' => $post->id,
            'commentID' => $lastComment->id,
            'replyID' => $comment->id,
            'type' => $post->type,
            'subType' => 'comment',
            'sub_type' => 'comment',
            'like_type' => $like_type ? $like_type->type : "",
            'total_comments' => $totalComments,
            'total_likes' => $totalLikes,
            'likes_types' =>  implode(",", $likes_types)
        ];
        $this->firebaseNotificationService->broadcastNotification($title, $body, $multicast);
        return $this->sendResponse($comment, "Comment posted", 201);
    }
    public function removeReply(Request $request, $id)
    {
        $user_id = Auth::user()->id;
        $comment = Comment::where(['id' => $id, 'user_id' => $user_id])->get()->first();
        if (is_null($comment)) {
            return   $this->sendError('Comment not found', [], 404);
        }
        $comment->delete();
        return $this->sendResponse(null, 'Comment deleted successfully', 204);
    }
    public function updateReply(Request $request, $id)
    {
        $user_id = Auth::user()->id;
        if (Auth::user()->is_blocked == '1') {
            return  $this->sendError('You are blocked. So, You can\'t comment on this post', [], 403);
        }
        $validator = Validator::make($request->all(), [
            'comment' => 'required',
        ]);
        if ($validator->fails()) {
            return $this->sendError($validator->errors()->first(), ['error' => $validator->errors()->first()], 400);
        }
        $comment = Comment::where(['id' => $id, 'user_id' => $user_id])->get()->first();
        if (is_null($comment)) {
            return   $this->sendError('Comment not found', [], 404);
        }
        $comment->comment = $request->comment;
        $comment->save();
        return $this->sendResponse($comment, 'Comment update successfully', 202);
    }
}
