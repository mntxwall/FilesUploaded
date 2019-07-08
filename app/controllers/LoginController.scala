package controllers

import java.util.Properties

import javax.inject.Inject
import javax.naming.{Context, NamingEnumeration}
import javax.naming.directory.{InitialDirContext, SearchControls, SearchResult}
import models.User
import play.api.{Configuration, Logger}
import play.api.data._
import play.api.data.Forms._
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.util.{Failure, Success, Try}

class LoginController @Inject()(cc: ControllerComponents, config: Configuration) extends AbstractController(cc){

  val logger: Logger = Logger(this.getClass())

  def validateForLDAP(user: User) = {

    Try {
      val props = new Properties
      props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory")
      props.put(Context.PROVIDER_URL, s"ldap://${config.get[String]("wei.login.address")}:${config.get[String]("wei.login.port")}")
      props.put(Context.SECURITY_PRINCIPAL, s"${user.username}@cloud.fzjz")
      props.put(Context.SECURITY_CREDENTIALS, user.password)

      new InitialDirContext(props)
    }
  }

  def findUserInfo(rtx: InitialDirContext, username: String) = {

    //   result match {
    //    case Success(v) => {
    var userDisplayName:Option[String] = None
    val constraints = new SearchControls
    constraints.setSearchScope(SearchControls.SUBTREE_SCOPE)
    val attrIDs = Array("distinguishedName", "displayname")

    constraints.setReturningAttributes(attrIDs)

    val answer: NamingEnumeration[SearchResult] = rtx.search("OU=INT,DC=cloud,DC=fzjz", "sAMAccountName=" + username, constraints)

    if (answer.hasMore) {
      val attrs = answer.next().getAttributes
      //println("distinguishedName " + attrs.get("distinguishedName"))
      //println("displayname " + attrs.get("displayname"))
      userDisplayName = Some(attrs.get("displayname").get().toString)
      logger.info(userDisplayName.getOrElse("Error"))
      println(userDisplayName)
    }
    userDisplayName

  }

  def test() = Action{

    Ok("ok")
  }
  def login() = Action(parse.multipartFormData){ implicit request =>

    val userLogin =  Form(
      mapping(
        "cloud-username" -> default(text, ""),
        "cloud-password" -> default(text, "")
      )(User.apply)(User.unapply)).bindFromRequest().get

    val loginResult = validateForLDAP(userLogin)
    loginResult match {
      case Success(value) => {
        Redirect("/test").withSession("gc_username" -> userLogin.username, "gc_displayname" -> findUserInfo(value, userLogin.username).getOrElse(""))
      }
      case Failure(exception) =>{
        println(exception)
        Redirect("/")
      }
    }

  }

  def logout() = Action{
    Redirect("/").withNewSession
  }

}
