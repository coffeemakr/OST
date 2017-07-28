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

import ch.unstable.transport.model.Capacity1st;
import ch.unstable.transport.model.Capacity2nd;
import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;

/**
 * A prognosis contains \&quot;realtime\&quot; informations on the status of a connection checkpoint.
 **/
@ApiModel(description = "A prognosis contains \"realtime\" informations on the status of a connection checkpoint.")
public class Prognosis {
  
  @SerializedName("platform")
  private Integer platform = null;
  @SerializedName("departure")
  private String departure = null;
  @SerializedName("arrival")
  private String arrival = null;
  @SerializedName("capacity1st")
  private Capacity1st capacity1st = null;
  @SerializedName("capacity2nd")
  private Capacity2nd capacity2nd = null;

  /**
   * The estimated arrival/departure platform
   **/
  @ApiModelProperty(value = "The estimated arrival/departure platform")
  public Integer getPlatform() {
    return platform;
  }
  public void setPlatform(Integer platform) {
    this.platform = platform;
  }

  /**
   * The departure time prognosis to the checkpoint. Date format: ISO 8601
   **/
  @ApiModelProperty(value = "The departure time prognosis to the checkpoint. Date format: ISO 8601")
  public String getDeparture() {
    return departure;
  }
  public void setDeparture(String departure) {
    this.departure = departure;
  }

  /**
   * The arrival time prognosis to the checkpoint. Date format: ISO 8601
   **/
  @ApiModelProperty(value = "The arrival time prognosis to the checkpoint. Date format: ISO 8601")
  public String getArrival() {
    return arrival;
  }
  public void setArrival(String arrival) {
    this.arrival = arrival;
  }

  /**
   **/
  @ApiModelProperty(value = "")
  public Capacity1st getCapacity1st() {
    return capacity1st;
  }
  public void setCapacity1st(Capacity1st capacity1st) {
    this.capacity1st = capacity1st;
  }

  /**
   **/
  @ApiModelProperty(value = "")
  public Capacity2nd getCapacity2nd() {
    return capacity2nd;
  }
  public void setCapacity2nd(Capacity2nd capacity2nd) {
    this.capacity2nd = capacity2nd;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Prognosis prognosis = (Prognosis) o;
    return (this.platform == null ? prognosis.platform == null : this.platform.equals(prognosis.platform)) &&
        (this.departure == null ? prognosis.departure == null : this.departure.equals(prognosis.departure)) &&
        (this.arrival == null ? prognosis.arrival == null : this.arrival.equals(prognosis.arrival)) &&
        (this.capacity1st == null ? prognosis.capacity1st == null : this.capacity1st.equals(prognosis.capacity1st)) &&
        (this.capacity2nd == null ? prognosis.capacity2nd == null : this.capacity2nd.equals(prognosis.capacity2nd));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.platform == null ? 0: this.platform.hashCode());
    result = 31 * result + (this.departure == null ? 0: this.departure.hashCode());
    result = 31 * result + (this.arrival == null ? 0: this.arrival.hashCode());
    result = 31 * result + (this.capacity1st == null ? 0: this.capacity1st.hashCode());
    result = 31 * result + (this.capacity2nd == null ? 0: this.capacity2nd.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class Prognosis {\n");
    
    sb.append("  platform: ").append(platform).append("\n");
    sb.append("  departure: ").append(departure).append("\n");
    sb.append("  arrival: ").append(arrival).append("\n");
    sb.append("  capacity1st: ").append(capacity1st).append("\n");
    sb.append("  capacity2nd: ").append(capacity2nd).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
