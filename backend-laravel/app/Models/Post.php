<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Post extends Model
{
    public function postedBy()
    {
        return $this->belongsTo(User::class, 'user_id')->select('id', 'name', 'profile_pic');
    }
    public function likes()
    {
        return $this->hasMany(PostLike::class);
    }
    public function comments()
    {
        return $this->hasMany(Comment::class);
    }
}
