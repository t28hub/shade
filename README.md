# Shade

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/3c7ef2214080460cbc33390143422e5d)](https://www.codacy.com/app/t28/shade?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=t28hub/shade&amp;utm_campaign=badger)
[![CircleCI](https://circleci.com/gh/t28hub/shade/tree/master.svg?style=shield&circle-token=25f82fe2b019fde78e4cd770177fe7108d8fe53e)](https://circleci.com/gh/t28hub/shade/tree/master)
[![Apache 2.0](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://github.com/t28hub/shade/blob/feature-updating-readme/LICENSE)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Shade-green.svg?style=true)](https://android-arsenal.com/details/1/4958)
[![Download](https://api.bintray.com/packages/t28/maven/shade/images/download.svg)](https://bintray.com/t28/maven/shade/_latestVersion)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/3c7ef2214080460cbc33390143422e5d)](https://www.codacy.com/app/t28/shade?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=t28hub/shade&amp;utm_campaign=badger)
[![Codacy Coverage](https://api.codacy.com/project/badge/Coverage/3c7ef2214080460cbc33390143422e5d)](https://www.codacy.com/app/t28/shade?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=t28hub/shade&amp;utm_campaign=badger)

![Shade](logo.png)  
Shade is a library makes SharedPreferences operation easy.

## Table of Contents
- [Background](#background)
- [Example](#example)
- [Installation](#installation)
- [Annotations](#annotations)
- [Converter](#converter)
- [Troubleshooting](#troubleshooting)
- [License](#license)

## Background
There might be a lot of boilerplate codes related to SharedPreferences operation in your Android application such as below.
Writing boilerplate codes is boring stuff and a waste of time.
In addition, it is necessary to review and test the bored codes.
Shade can solve the problem.
Shade generates codes related to `SharedPreferences` operation automatically once you only defines an interface or an abstract class.
Generated codes run safe and fast because Shade does not use the Reflection.

## Example
As you can see below, shade reduces a lot of boilerplate codes.
In this example, user name and user age are stored into the `SharedPreferences`.

### Before
Here is an example implementation before using Shade.
```java
public class UserUtils {
    private static final String PREF_NAME = "io.t28.example.user";
    private static final String PREF_USER_NAME = "user_name";
    private static final String PREF_USER_NAME = "user_age";
    private static final String DEFAULT_USER_NAME = "guest";
    private static final int DEFAULT_USER_AGE = 20;

    public static boolean hasName(Context context) {
        return getSharedPreferences(context).contains(PREF_USER_NAME);
    }

    public static String getName(Context context) {
        return getSharedPreferences(context).getString(PREF_USER_NAME, DEFAULT_USER_NAME);
    }

    public static void putName(Context context, String value) {
       getSharedPreferences(context).edit().putString(PREF_USER_NAME, value).apply();
    }

    public static void removeName(Context context) {
        return getSharedPreferences(context).edit().remove(USER_NAME).apply();
    }

    public static boolean hasAge(Context context) {
        return getSharedPreferences(context).contains(PREF_USER_AGE);
    }

    public static int getAge(Context context) {
        return getSharedPreferences(context).getInt(PREF_USER_AGE, DEFAULT_USER_AGE);
    }

    public static void putAge(Context context, int value) {
        return getSharedPreferences(context).edit().putInt(PREF_USER_AGE, value).apply();
    }

    public static void removeAge(Context context) {
        return getSharedPreferences(context).edit().remove(PREF_USER_AGE).apply();
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Cotnext.MODE_PRIVATE);
    }
}
```

### After
Here is an example implementation after using Shade.
```java
@Preferences("io.t28.example.user")
public interface class User {
    @Property(key = "user_name", defValue = "guest")
    String name();

    @Property(key = "user_age", defValue = "20")
    int age();
}
```
Shade generates 3 classes.

1. `UserPreferences`
1. `UserPreferences.Editor`
1. `UserPreferences.UserImpl`

You can use the generated classes as below.
```java
// Instantiate the UserPreferences
UserPreferences preferences = new UserPreferences(context);

// Get preferences as a model
User user = preference.get(); // UserImpl{name=guest, age=20}

// Check whether a specific preference is contained
preferences.containsName(); // false
preferences.containsAge();  // false

// Get a specific preference
String name = preferences.getName(); // guest
int age = preferences.getAge(); // 20

// Put a specific preference
preferences.edit()
        .putName("My name")
        .putAge(30)
        .apply();

// Put a model
User newUser = new UserPreferences.UserImpl("My name", 30);
preferences.edit()
        .put(newUser)
        .apply();

// Remove a specific preference
preferences.edit()
        .removeName()
        .removeAge()
        .apply();

// Clear all preferences
preferences.edit()
        .clear()
        .apply();
```

## Installation
```
dependencies {
    compile 'io.t28:shade:0.9.0'
    annotationProcessor 'io.t28:shade-processor:0.9.0'
}
```

## Annotations
Shade provides only 2 annotations. One is `@Preferences` and the other is `@Property`.

### `@Preferences`
`@Preferences` can be used to declare `SharedPreferences` model, and it can be annotated for an abstract class or an interface.

| Parameter | Type | Default Value | Description |
|:---|:---|:---|:---|:---|
| value | `String` | `""` | Alias for name which allows to ignore `name=` part |
| name | `String` | `""` | The name of SharedPreferences |
| mode | `int` | `Context.MODE_PRIVATE` | The operating mode of SharedPreferences |

* Generated preference class uses the default SharedPreferences if you do not specify `value` and `name`.
* A value specified with `value` is used by generated preference class if both `value` and `name` is specified.
* Although you can specify the following values as a mode, Android official document suggest to use `Context.MODE_PRIVATE` if there is no any special reasons.
 * [Context.MODE_PRIVATE](https://developer.android.com/reference/android/content/Context.html#MODE_PRIVATE)
 * [Context.MODE_WORLD_READABLE](https://developer.android.com/reference/android/content/Context.html#MODE_WORLD_READABLE)
 * [Context.MODE_WORLD_WRITEABLE](https://developer.android.com/reference/android/content/Context.html#MODE_WORLD_WRITEABLE)
 * [Context.MODE_MULTI_PROCESS](https://developer.android.com/reference/android/content/Context.html#MODE_MULTI_PROCESS)

Here is the example which uses `io.t28.shade.example` as a name and `Context.MODE_PRIVATE` as a mode.
```java
@Preferences(name = "io.t28.shade.example", mode = Context.MODE_PRIVATE)
public abstract class Example {
}
```

### `@Property`
`@Property` can be used to declare `SharedPreferences` key, and it can be annotated for an abstract method.

| Parameter | Type | Default Value | Description |
|:---|:---|:---|:---|:---|
| value | `String` | `""` | Alias for name which allows to ignore `key=` part |
| key | `String` | `""` | The key of the preference value |
| defValue | `String` | `""` | The default value for the key |
| converter | `Class<? extends Converter>` | `Converter.class` | The converter that converts any value to supported value |

* Either `value` or `key` must be specified.
* `defValue` will be parsed as a type of return type.
* For example, `defValue` will be parsed as `boolean` if a method annotated with `@Property` returns boolean value.
* Converter is useful for you if you need to store unsupported type to the `SharedPreferences`.
* The details of the Converter is mentioned in the below section.

Here is the example which used `count` as a key and `1` as a default value.
```java
@Preferences
public abstract class Example {
    @Property(key = "count", defValue = "1")
    public abstract int count();
}
```

The following default values are used if `defValue` is not specified.

|Type | Default value |
|:---|:---|
| `boolean` | `false` |
| `float` | `0.0f` |
| `int` | `0` |
| `long` | `0L` |
| `String` | `""` |
| `Set<String>` | `Collections.emptySet()` |

## Converter
`SharedPreferences` allows to store only 6 types as below.

1. `boolean`
1. `float`
1. `int`
1. `long`
1. `String`
1. `Set<String>`

`Converter` allows you to store unsupported types to `SharedPreferences`.
You need to implement a converter which converts `java.lang.Double` to `java.lang.Long`, if you would like to use `java.lang.Double`.
Here is an example implementation.
```java
public class DoubleConverter implements Converter<Double, Long> {
    private static final double DEFAULT_VALUE = 0.0d;

    @NonNull
    @Override
    public Double toConverted(@Nullable Long supported) {
        if (supported == null) {
            return DEFAULT_VALUE;
        }
        return Double.longBitsToDouble(supported);
    }

    @NonNull
    @Override
    public Long toSupported(@Nullable Double converted) {
        if (converted == null) {
            return Double.doubleToLongBits(DEFAULT_VALUE);
        }
        return Double.doubleToLongBits(converted);
    }
}
```
Converter class should provide a default constructor.  
`toConverted` should convert supported value to converted value and `toSupported` should convert converted value to supported value.
You need to specify the `DoubleConverter` to the `@Property` such as below.
```java
@Preferences
public abstract class Example {
    @Property(key = "updated", converter = DoubleConverter.class)
    public abstract Date updated();
}
```

## Troubleshooting
Feel free to ask me if there is any troubles.

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
