/*
 * Copyright 2016-2018 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package github4s.effect.js

import cats.effect.Async
import fr.hmil.roshttp.response.SimpleHttpResponse
import github4s.HttpRequestBuilderExtensionJS
import github4s.effect.{AsyncHttpRequestBuilderExtensionJS, SyncCaptureInstance}
import github4s.free.interpreters.Interpreters
import github4s.implicits._

trait ImplicitsJS extends AsyncHttpRequestBuilderExtensionJS with SyncCaptureInstance {
  implicit def intInstanceAsyncRosHttp[F[_]: Async] =
    new Interpreters[F, SimpleHttpResponse]
}
