## Welcome to SimpleRESTIntegration 

in this section we will explain how to use the simple library for get data from other resources

for use this library you must adding dependency to your pom in your project, with

```xml
<dependency>
    <groupId>id.web.widat</groupId>
    <artifactId>SimpleRESTIntegration</artifactId>
    <version>0.0.2</version>
</dependency>
```

1. POST example

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

	public static void test1(){

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		Map<String,String> data = new HashMap<String,String>();
		data.put("nama", "widat");
		data.put("alamat", "pati");

		Map<String,String> property = new HashMap<String,String>();
		property.put("Content-Type", "application/x-www-form-urlencoded");

		Response response = RESTClient.pull(Protocol.HTTP, "http://httpbin.org/post", data, Method.POST, property);

		ResponseData responseData = gson.fromJson(response.getResult(), ResponseData.class);

		System.out.println(gson.toJson(responseData));

	}

	public static void main(String[] args) {
		App.test1();
	}
}
```
result:

```json
{
  "form": {
    "nama": "widat",
    "alamat": "pati"
  }
}

```


You can use the [editor on GitHub](https://github.com/wid4t/SimpleRESTIntegration/edit/master/README.md) to maintain and preview the content for your website in Markdown files.

Whenever you commit to this repository, GitHub Pages will run [Jekyll](https://jekyllrb.com/) to rebuild the pages in your site, from the content in your Markdown files.

### Markdown

Markdown is a lightweight and easy-to-use syntax for styling your writing. It includes conventions for

```markdown
Syntax highlighted code block

# Header 1
## Header 2
### Header 3

- Bulleted
- List

1. Numbered
2. List

**Bold** and _Italic_ and `Code` text

[Link](url) and ![Image](src)
```

For more details see [GitHub Flavored Markdown](https://guides.github.com/features/mastering-markdown/).

### Jekyll Themes

Your Pages site will use the layout and styles from the Jekyll theme you have selected in your [repository settings](https://github.com/wid4t/SimpleRESTIntegration/settings). The name of this theme is saved in the Jekyll `_config.yml` configuration file.

### Support or Contact

Having trouble with Pages? Check out our [documentation](https://help.github.com/categories/github-pages-basics/) or [contact support](https://github.com/contact) and we’ll help you sort it out.
