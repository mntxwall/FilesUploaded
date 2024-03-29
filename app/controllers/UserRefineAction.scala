package controllers

import javax.inject.Inject
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class UserRequest[A](val username: Option[String], request: Request[A]) extends WrappedRequest[A](request)

class UserRefineAction @Inject()(val parser: BodyParsers.Default)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[Request, AnyContent] with ActionRefiner[Request, UserRequest]{


  override protected def refine[A](request: Request[A]):Future[Either[Result, UserRequest[A]]] = {

    val result = request.session.get("gc_username")
      .map(user => new UserRequest[A](Option(user), request))
      .toRight(Results.Ok(views.html.login()))

    //val aa = Left(Results.Ok(views.html.login()))
    //val result = new UserRequest[A](Option("bbbb"), request)
    Future.successful{
      result
      //aa
    }

  }
}
