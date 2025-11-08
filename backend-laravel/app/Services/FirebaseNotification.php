<?php

namespace App\Services;

use Kreait\Firebase\Factory;
use Kreait\Firebase\Messaging\CloudMessage;
use Kreait\Firebase\Messaging\Notification;
use Kreait\Firebase\Messaging\AndroidConfig;


class FirebaseNotification
{
    protected $messaging;
    public function __construct()
    {
        // $factory = (new Factory)->withServiceAccount(config('firebase.credentials_file'));
        $factory = (new Factory)->withServiceAccount(__DIR__ . '/../../config/firebase_credentials.json');
        $this->messaging = $factory->createMessaging();
    }
    public function sendToMultipleDevices(array $tokens, string $title, string $body, array $data)
    {

        $androidConfig = AndroidConfig::fromArray([
            'priority' => 'high',
            'notification' => [
                'title' => $title,
                'body' => $body,
                'image' => $data['type'] ?? NULL,
            ],
        ]);
        $messages = [];
        foreach ($tokens as $token) {
            $messages[] = CloudMessage::withTarget('token', $token)
                // ->withAndroidConfig($androidConfig)
                // ->withNotification(['title' => $title, 'body' => $body, 'image' => $data['type'] ?? NULL])
                ->withData($data);
        }
        $this->messaging->sendAll($messages);
    }
    public function broadcastNotification(string $title, string $body, array $data)
    {
        $message = CloudMessage::fromArray([
            'topic' => 'krishna',
            // "notification" => [
            //     "title" => $title,
            //     "body" => $body
            // ],
            'data' => $data,
            "priority" => "high"
        ]);
        $this->messaging->send($message);
    }


    /* 
    private $firebase, $messaging;
    public function __construct()
    {
        $this->firebase = 
        $this->messaging = $this->firebase->createMessaging();
    }
    //TODO Start working form here
    public function sendPushNotification($token, $title, $body)
    {
        $message = CloudMessage::fromArray([
            'token' => $token,
            'notification' => [
                'title' => $title,
                'body' =>  $body
            ],
        ]);
        return $this->messaging->send($message);
    }
    public function sendMulticastPushNotification($tokens, $title, $body)
    {
        $notification = Notification::create($title, $body);

        $messages = array_map(function ($token) use ($notification) {
            return CloudMessage::withTarget('token', $token)->withNotification($notification);
        }, $tokens);
        return $this->messaging->sendAll($messages);
    }
        
        $firebase = (new Factory)->withServiceAccount(__DIR__.’/../../../config/firebase_credentials.json’);

        $messaging = $firebase->createMessaging();

        $message = CloudMessage::fromArray([
            'notification' => [
                'title' => 'Hello from Firebase!',
                'body' => 'This is a test notification.'
            ],
            'topic' => 'global'
        ]);

        $messaging->send($message);

        return response()->json(['message' => 'Push notification sent successfully']);
    }
    
    
    
    */
}
