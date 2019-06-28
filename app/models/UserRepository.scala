package models

import javax.inject.Inject
import play.api.db.DBApi
import anorm.SQL


class UserRepository @Inject()(dbApi: DBApi){
  private val db = dbApi.database("default")

  def test() = {
    db.withConnection{ implicit c =>
      SQL("INSERT INTO LOCKS(USERS) VALUES ({v1})").on("v1" -> "Mary").execute()
    }

  }

}
