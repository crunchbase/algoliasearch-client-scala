/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Algolia
 * http://www.algolia.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package algolia.http

import java.net.InetAddress

import io.netty.resolver.NameResolver
import com.netaporter.uri.dsl._
import org.asynchttpclient.{Request, RequestBuilder}

private[algolia] sealed trait HttpVerb

private[algolia] case object GET extends HttpVerb {
  override def toString: String = "GET"
}

private[algolia] case object POST extends HttpVerb {
  override def toString: String = "POST"
}

private[algolia] case object PUT extends HttpVerb {
  override def toString: String = "PUT"
}

private[algolia] case object DELETE extends HttpVerb {
  override def toString: String = "DELETE"
}

private[algolia] case class HttpPayload(verb: HttpVerb,
                                        path: Seq[String],
                                        queryParameters: Option[Map[String, String]] = None,
                                        body: Option[String] = None,
                                        isSearch: Boolean = true) {

  def apply(host: String, headers: Map[String, String], dnsNameResolver: NameResolver[InetAddress]): Request = {
    val uri = path.foldLeft(host)((url, p) => url / p)

    var builder: RequestBuilder = new RequestBuilder().setMethod(verb.toString).setUrl(uri)

    headers.foreach { case (k, v) => builder = builder.addHeader(k, v) }

    queryParameters.foreach(
      _.foreach { case (k, v) => builder = builder.addQueryParam(k, v) }
    )

    body.foreach(b => builder = builder.setBody(b))

    builder.setNameResolver(dnsNameResolver).build()
  }

}
