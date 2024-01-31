package jinproject.stepwalk.data.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

//    @Provides
//    @Singleton
//    fun providesDatabase(@ApplicationContext context: Context): Database {
//        return Room.databaseBuilder(context, Database::class.java, "myDatabase")
//            .createFromAsset("stepMate.db")//초기 미션 파일을 등록?
//            .build()
//    }

}