import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.githubproject.data.local.entity.FavoriteUser
import com.example.githubproject.data.local.room.FavoriteUserRoomDatabase

class FavoriteUserViewModel(application: Application) : AndroidViewModel(application) {

    private val favoriteUserRoomDatabase: FavoriteUserRoomDatabase =
        FavoriteUserRoomDatabase.getDatabase(application.applicationContext)

    val favoriteUsers: LiveData<List<FavoriteUser>> =
        favoriteUserRoomDatabase.favoriteUserDao().getAllUsers()
}
