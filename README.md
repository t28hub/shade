# Shade
[![CircleCI](https://circleci.com/gh/t28hub/shade/tree/master.svg?style=shield&circle-token=25f82fe2b019fde78e4cd770177fe7108d8fe53e)](https://circleci.com/gh/t28hub/shade/tree/master)
[![Apache 2.0](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://github.com/t28hub/shade/blob/feature-updating-readme/LICENSE)
[![Codacy Grade](https://api.codacy.com/project/badge/Grade/b4aad6ede42c43678389cb7a915dd1a7)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=t28hub/shade&amp;utm_campaign=Badge_Grade)
[![Codacy Coverage](https://api.codacy.com/project/badge/Coverage/b4aad6ede42c43678389cb7a915dd1a7)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=t28hub/shade&amp;utm_campaign=Badge_Coverage)

Shade is a library makes SharedPreferences operation easy.
There are only 2 annotations you should know when you use this.

## Table of Contents
- [Background](#background)
- [License](#license)

## Background
There might be a lot of boilerplate code for SharedPreferences operation in your android application.
Generally speaking, the code needs to be tested and reviewed if it is written manually.
However, the code does not need to be tested and reviewed if it is generated automatically.
Shade generates a lot of boilerplate code for SharedPreferences operation automatically using annotation processing and not reflection.
Therefore it has potential to make your android application development safe and efficient.

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
