package com.example.happyplaces.room


import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName ="happy_places")
class happyPlacesData(

                      @ColumnInfo(name="title") val title:String?,
                      @ColumnInfo(name="description") val description:String?,
                      @ColumnInfo(name="date") val date:String?,
                      @ColumnInfo(name="location") val location:String?,
                      @ColumnInfo(name="image") val image: String?,
                      @ColumnInfo(name="latitude") val latitude:Double,
                      @ColumnInfo(name="longitude") val longitude:Double
                      ):Parcelable
                      {


                        @PrimaryKey(autoGenerate = true) var id=0

                          constructor(parcel: Parcel) : this(
                              parcel.readString(),
                              parcel.readString(),
                              parcel.readString(),
                              parcel.readString(),
                              parcel.readString(),
                              parcel.readDouble(),
                              parcel.readDouble()
                          ) {
                              id = parcel.readInt()
                          }

                          override fun writeToParcel(parcel: Parcel, flags: Int) {
                              parcel.writeString(title)
                              parcel.writeString(description)
                              parcel.writeString(date)
                              parcel.writeString(location)
                              parcel.writeString(image)
                              parcel.writeDouble(latitude)
                              parcel.writeDouble(longitude)
                              parcel.writeInt(id)
                          }

                          override fun describeContents(): Int {
                              return 0
                          }

                          companion object CREATOR : Parcelable.Creator<happyPlacesData> {
                              override fun createFromParcel(parcel: Parcel): happyPlacesData {
                                  return happyPlacesData(parcel)
                              }

                              override fun newArray(size: Int): Array<happyPlacesData?> {
                                  return arrayOfNulls(size)
                              }
                          }


                      }