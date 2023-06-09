import { Action } from 'redux';

export interface LatLng {
  readonly lat: number;
  readonly lng: number;
}

export interface LatLngWithDescription extends LatLng {
  description: string;
}

export interface Location extends LatLng {
  readonly id: number;
  // TODO decide between optional, nullable and more complex structure (displayName, fullDescription, address, ...)
  readonly description?: string;
}

export interface Vehicle {
  readonly id: number;
  readonly name: string;
  readonly capacity: number;
}

export interface Route {
  readonly vehicle: Vehicle; // TODO change to vehicleId
  readonly visits: Location[];
}

export type LatLngTuple = [number, number];

export interface RouteWithTrack extends Route {
  readonly track: LatLngTuple[];
}

export interface RoutingPlan {
  readonly distance: string;
  readonly vehicles: Vehicle[];
  readonly depot: Location | null;
  readonly visits: Location[];
  readonly routes: RouteWithTrack[];
}

export enum ActionType {
  UPDATE_ROUTING_PLAN = 'UPDATE_ROUTING_PLAN',
  DELETE_LOCATION = 'DELETE_LOCATION',
  ADD_LOCATION = 'ADD_LOCATION',
  ADD_VEHICLE = 'ADD_VEHICLE',
  DELETE_VEHICLE = 'DELETE_VEHICLE',
  CLEAR_SOLUTION = 'CLEAR_SOLUTION',
}

export interface AddLocationAction extends Action<ActionType.ADD_LOCATION> {
  readonly value: LatLngWithDescription;
}

export interface AddVehicleAction extends Action<ActionType.ADD_VEHICLE> {
}

export interface ClearRouteAction extends Action<ActionType.CLEAR_SOLUTION> {
}

export interface DeleteLocationAction extends Action<ActionType.DELETE_LOCATION> {
  readonly value: number;
}

export interface DeleteVehicleAction extends Action<ActionType.DELETE_VEHICLE> {
  readonly value: number;
}

export interface VehicleCapacity {
  vehicleId: number;
  capacity: number;
}

export interface UpdateRouteAction extends Action<ActionType.UPDATE_ROUTING_PLAN> {
  readonly plan: RoutingPlan;
}

export type RouteAction =
  | AddLocationAction
  | AddVehicleAction
  | DeleteLocationAction
  | DeleteVehicleAction
  | UpdateRouteAction
  | ClearRouteAction;
