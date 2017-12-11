# API Doc

## 获取房间列表

| Path       | Method | Param            | Description |
| ---------- | ------ | :--------------- | ----------- |
| /room/list | GET    | limit<br/>offset | 获取所有房间列表    |

### Response Body

```Json
{
    "status": 0,
    "message": "success",
    "data": {
        "rooms": [
            {
                "_id": "5a255cbe2cadd56eddda572f",
                "room_name": "105",
                "positions": [
                    {
                        "wifi_stats": [
                            {
                                "BSSID": "14:75:90:24:5c:1c",
                                "RSSI": -53,
                                "SSID": "Hello_W",
                                "capabilities": "[WPA-PSK-CCMP][WPA2-PSK-CCMP][WPS][ESS]",
                                "channel": 1,
                                "frequency": "LOW"
                            },
                            {
                                "BSSID": "14:75:90:18:2f:6e",
                                "RSSI": -62,
                                "SSID": "100007",
                                "capabilities": "[WPA-PSK-CCMP][WPA2-PSK-CCMP][WPS][ESS]",
                                "channel": 1,
                                "frequency": "LOW"
                            },
                            {
                                "BSSID": "c8:e7:d8:3e:03:d0",
                                "RSSI": -59,
                                "SSID": "9#103",
                                "capabilities": "[WPA-PSK-CCMP][WPA2-PSK-CCMP][WPS][ESS]",
                                "channel": 11,
                                "frequency": "LOW"
                            },
                            {
                                "BSSID": "b8:f8:83:e0:1e:7d",
                                "RSSI": -80,
                                "SSID": "101",
                                "capabilities": "[WPA-PSK-CCMP][WPA2-PSK-CCMP][ESS]",
                                "channel": 4,
                                "frequency": "LOW"
                            }
                        ],
                        "x": 0,
                        "y": 0
                    },
                    ...
                ]
            },
            {
                "_id": "5a2bc9ef5a117a598859f3b8",
                "room_name": "hahaha",
                "positions": []
            }
        ]
    }
}
```



## 获取房间信息

| Path       | Method | Param   | Description |
| ---------- | ------ | :------ | ----------- |
| /room/info | GET    | room_id | 获取房间信息      |

### Response Body

```Json
{
    "status": 0,
    "message": "success",
    "data": {
        "room": {
            "_id": "5a255cbe2cadd56eddda572f",
            "room_name": "105",
            "positions": [
                {
                    "wifi_stats": [
                        {
                            "BSSID": "14:75:90:24:5c:1c",
                            "RSSI": -53,
                            "SSID": "Hello_W",
                            "capabilities": "[WPA-PSK-CCMP][WPA2-PSK-CCMP][WPS][ESS]",
                            "channel": 1,
                            "frequency": "LOW"
                        },
                        {
                            "BSSID": "14:75:90:18:2f:6e",
                            "RSSI": -62,
                            "SSID": "100007",
                            "capabilities": "[WPA-PSK-CCMP][WPA2-PSK-CCMP][WPS][ESS]",
                            "channel": 1,
                            "frequency": "LOW"
                        },
                        {
                            "BSSID": "c8:e7:d8:3e:03:d0",
                            "RSSI": -59,
                            "SSID": "9#103",
                            "capabilities": "[WPA-PSK-CCMP][WPA2-PSK-CCMP][WPS][ESS]",
                            "channel": 11,
                            "frequency": "LOW"
                        },
                        {
                            "BSSID": "b8:f8:83:e0:1e:7d",
                            "RSSI": -80,
                            "SSID": "101",
                            "capabilities": "[WPA-PSK-CCMP][WPA2-PSK-CCMP][ESS]",
                            "channel": 4,
                            "frequency": "LOW"
                        }
                    ],
                    "x": 0,
                    "y": 0
                },
                ...
            ]
        }
    }
}
```

## 新建房间

| Path      | Method | Param | Description |
| --------- | ------ | :---- | ----------- |
| /room/new | POST   | name  | 新建房间        |

### Response Body

```json
{
    "status": 0,
    "message": "success",
    "data": {
        "room_id": "5a2bc9ef5a117a598859f3b8"
    }
}
```

## 删除房间

| Path         | Method | Param   | Description |
| ------------ | ------ | :------ | ----------- |
| /room/delete | POST   | room_id | 删除房间        |

### Response Body

```json
{
    "status": 0,
    "message": "success"
}
```

## 清空房间位置信息

| Path                  | Method | Param   | Description |
| --------------------- | ------ | :------ | ----------- |
| /room/positions/clear | POST   | room_id | 清空房间位置信息    |

### Response Body

```json
{
    "status": 0,
    "message": "success"
}
```

## 上传WiFi位置信息

| Path         | Method | Param   | Description     |
| ------------ | ------ | :------ | --------------- |
| /wifi/upload | POST   | room_id | 上传WiFi位置信息至指定房间 |

### Request Body

```json
{
	"x":52343,
	"y":662,
	"wifi_stats":[
		{
			"SSID":"Hello_w",
			"BSSID":"14:75:90:24:5c:1c",
			"RSSI":-65,
			"capabilities":"ESS",
			"channel":1,
			"frequency":"2.4 GHz"
		},
       ...
	]
}
```

### Response Body

```json
{
    "status": 0,
    "message": "success"
}
```

## 计算当前位置

| Path           | Method | Param   | Description    |
| -------------- | ------ | :------ | -------------- |
| /room/location | POST   | room_id | 根据当前WiFi信息计算位置 |

### Request Body

```json
{
	"wifi_stats":[
		{
			"SSID":"Hello_w",
			"BSSID":"14:75:90:24:5c:1c",
			"RSSI":-65,
			"capabilities":"ESS",
			"channel":1,
			"frequency":"2.4 GHz"
		},
       ...
	]
}
```

### Response Body

```json
{
    "status": 0,
    "message": "success"
}
```

