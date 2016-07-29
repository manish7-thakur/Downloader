package com.agoda.rest

import spray.http.StatusCodes
import spray.routing.HttpService

/**
 * Created by mthakur on 29/07/16.
 */
trait DownloadRoute extends HttpService {
  def downloadRoute = pathPrefix("download") {
    post {
      complete(StatusCodes.OK)
    }
  }
}
