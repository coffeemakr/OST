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

import ch.unstable.transport.model.Checkpoint;
import ch.unstable.transport.model.Journey;
import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;

/**
 * A connection consists of one or multiple sections.
 **/
@ApiModel(description = "A connection consists of one or multiple sections.")
public class Sections {
  
  @SerializedName("journey")
  private Journey journey = null;
  @SerializedName("departure")
  private Checkpoint departure = null;
  @SerializedName("arrival")
  private Checkpoint arrival = null;

  /**
   * A journey, the transportation used by this section. Can be null
   **/
  @ApiModelProperty(value = "A journey, the transportation used by this section. Can be null")
  public Journey getJourney() {
    return journey;
  }
  public void setJourney(Journey journey) {
    this.journey = journey;
  }

  /**
   **/
  @ApiModelProperty(value = "")
  public Checkpoint getDeparture() {
    return departure;
  }
  public void setDeparture(Checkpoint departure) {
    this.departure = departure;
  }

  /**
   **/
  @ApiModelProperty(value = "")
  public Checkpoint getArrival() {
    return arrival;
  }
  public void setArrival(Checkpoint arrival) {
    this.arrival = arrival;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Sections sections = (Sections) o;
    return (this.journey == null ? sections.journey == null : this.journey.equals(sections.journey)) &&
        (this.departure == null ? sections.departure == null : this.departure.equals(sections.departure)) &&
        (this.arrival == null ? sections.arrival == null : this.arrival.equals(sections.arrival));
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + (this.journey == null ? 0: this.journey.hashCode());
    result = 31 * result + (this.departure == null ? 0: this.departure.hashCode());
    result = 31 * result + (this.arrival == null ? 0: this.arrival.hashCode());
    return result;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class Sections {\n");
    
    sb.append("  journey: ").append(journey).append("\n");
    sb.append("  departure: ").append(departure).append("\n");
    sb.append("  arrival: ").append(arrival).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
