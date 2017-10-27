## Welcome to SimpleRESTIntegration 

in this section we will explain how to use the simple library for get data from other resources

for use this library you must adding dependency to your pom in your project, with

```xml
<dependency>
    <groupId>id.web.widat</groupId>
    <artifactId>SimpleRESTIntegration</artifactId>
    <version>0.0.2</version>
</dependency>

<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.2</version>
</dependency>	  
```

1. POST

```java
package com.data.simplerest;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import id.web.widat.SimpleRESTIntegration.RESTClient;
import id.web.widat.SimpleRESTIntegration.constants.Method;
import id.web.widat.SimpleRESTIntegration.constants.Protocol;
import id.web.widat.SimpleRESTIntegration.model.Response;

public class App {

	public static void testHttpPost(){

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		Map<String,String> data = new HashMap<String,String>();
		data.put("nama", "widat");
		data.put("alamat", "pati");

		Map<String,String> property = new HashMap<String,String>();
		property.put("Content-Type", "application/x-www-form-urlencoded");

		Response response = RESTClient.pull(Protocol.HTTP, "http://httpbin.org/post", data, Method.POST, property);

		System.out.println(gson.toJson(response));

	}

	public static void main(String[] args) {
		App.testHttpPost();
	}

}

```
result:

```json
{
  "code": 200,
  "message": "OK",
  "result": "{  \"args\": {},   \"data\": \"\",   \"files\": {},   \"form\": {    \"alamat\": \"pati\",     \"nama\": \"widat\"  },   \"headers\": {    \"Accept\": \"text/html, image/gif, image/jpeg, *; q\u003d.2, */*; q\u003d.2\",     \"Connection\": \"close\",     \"Content-Length\": \"22\",     \"Content-Type\": \"application/x-www-form-urlencoded\",     \"Host\": \"httpbin.org\",     \"User-Agent\": \"Java/1.8.0_91\"  },   \"json\": null,   \"origin\": \"115.178.215.135\",   \"url\": \"http://httpbin.org/post\"}"
}
