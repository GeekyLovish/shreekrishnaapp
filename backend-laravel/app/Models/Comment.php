<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Comment extends Model
{
    public function commentedBy()
    {
        return $this->belongsTo(User::class, 'user_id')->select('id', 'name', 'profile_pic', 'is_blocked');
    }
    public function commentLikes()
    {
        return $this->hasMany(CommentLike::class);
    }
    public function replies()
    {
        return $this->hasMany(Comment::class, 'comment_id')->with('commentedBy')->withCount('commentLikes');
    }
}
