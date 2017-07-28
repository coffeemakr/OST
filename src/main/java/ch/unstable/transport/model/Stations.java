/**
 * Trasport API
 * The Transport API allows interested developers to build their own applications using public timetable data, whether they're on the web, the desktop or mobile devices. The aim of this inofficial API is to cover public transport within Switzerland. If you are looking for an officially supported source or need to download all data e.g in GTFS format, please check opendata.swiss. The source code of the Transport API can be found on GitHub, please ask any technical questions there. If you need a direct contact write an email to transport@opendata.ch. In order to be kept update on the future development of this API, please subscribe to our low-traffic Google Group.
 *
 * OpenAPI spec version: 1
 * Contact: transport@opendata.ch
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ch.unstable.transport.model;

import ch.unstable.transport.model.Location;
import java.util.*;
import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;

@ApiModel(description = "")
public class Stations {
  
  @SerializedName("stations")
  private List<Location> stations = null;

  /**
   **/
  @ApiModelProperty(value = "")
  public List<Location> getStations() {
    return stations;
  }
  public void setStations(List<Location> stations) {
    this.stations = stations;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Stations stations = (Stations) o;
    return (this.stations == null ? stations.stations == null : this.stations.equals(stations.stations));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.stations == null ? 0: this.stations.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class Stations {\n");
    
    sb.append("  stations: ").append(stations).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
