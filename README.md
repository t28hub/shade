# Shade
[![CircleCI](https://circleci.com/gh/t28hub/shade/tree/master.svg?style=shield&circle-token=25f82fe2b019fde78e4cd770177fe7108d8fe53e)](https://circleci.com/gh/t28hub/shade/tree/master)
[![Apache 2.0](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://github.com/t28hub/shade/blob/feature-updating-readme/LICENSE)
[![Download](https://api.bintray.com/packages/t28/maven/shade/images/download.svg)](https://bintray.com/t28/maven/shade/_latestVersion)
[![Codacy Grade](https://api.codacy.com/project/badge/Grade/b4aad6ede42c43678389cb7a915dd1a7)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=t28hub/shade&amp;utm_campaign=Badge_Grade)
[![Codacy Coverage](https://api.codacy.com/project/badge/Coverage/b4aad6ede42c43678389cb7a915dd1a7)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=t28hub/shade&amp;utm_campaign=Badge_Coverage)

Shade is a library makes SharedPreferences operation easy.

## Table of Contents
- [Background](#background)
- [Example](#example)
- [Installation](#installation)
- [License](#license)

## Background
There might be a lot of boilerplate code for SharedPreferences operation in your android application.  
Generally speaking, the code needs to be tested and reviewed if it is written manually.  
However, the code does not need to be tested and reviewed if it is generated automatically.  
Shade generates a lot of boilerplate code for SharedPreferences operation automatically using annotation processing and not reflection.  
Therefore it has potential to make your android application development safe and efficient.  

## Example
If you want to store user name and age to a SharedPreferences named `io.t28.shade.example`, you only need to define a following class.
```java
@Preferences(name = "io.t28.shade.example")
public abstract class User {
    @Property(name = "user_name", defValue = "guest")
    String name();

    @Property(name = "user_age", defValue = "18")
    int age();

    @NonNull
    public static UserPreferences getPreferences(@NonNull Context context) {
        return new UserPreferences(context);
    }
}
```
Shade will generate a `UserPreferences` and you can use it such as below.
```java
final UserPreferences preferences = User.getPreferences(context);
final User user = preferences.get();
final String name = preferences.getName();
final int age = preferences.getAge();

preferences.edit()
        .putName("t28")
        .removeAge()
        .apply();
```


## Installation
```
dependencies {
    compile 'io.t28:shade:0.1.0'
    annotationProcessor 'io.t28:shade-processor:0.1.0'
}
```

## License
```
Copyright (c) 2016 Tatsuya Maki

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
