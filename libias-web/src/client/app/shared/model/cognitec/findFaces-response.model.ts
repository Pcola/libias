import { FaceLocation } from "./faceLocation.model";

export interface FindFacesResponse {
  val: FaceLocations;
}

interface FaceLocations {
  faces: FaceLocation[];
}