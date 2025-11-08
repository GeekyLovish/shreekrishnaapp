<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class PostLike extends Model
{
    public function likedBy()
    {
        return $this->belongsTo(User::class, 'user_id')->select('id', 'name', 'profile_pic');
    }
}
