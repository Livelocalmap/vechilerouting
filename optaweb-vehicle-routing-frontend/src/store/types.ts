import { Action } from 'redux';
import { ThunkAction } from 'redux-thunk';
import { Message } from 'store/message/types';
import WebSocketClient from 'websocket/WebSocketClient';
import { UserViewport } from './client/types';
import { Demo } from './demo/types';
import { RoutingPlan } from './route/types';
import { ServerInfo } from './server/types';
import { WebSocketConnectionStatus } from './websocket/types';

/**
 * ThunkCommand is a ThunkAction that has no result (it's typically something like
 * `Promise<ActionAfterDataFetched>`, but sending messages over WebSocket usually has no response
 * (with the exception of subscribe), so most of our operations are void).
 *
 * @template A Type of action(s) allowed to be dispatched.
 */
export type ThunkCommand<A extends Action> = ThunkAction<void, AppState, WebSocketClient, A>;

/**
 * Factory method that takes a value and creates an @type {Action}.
 *
 * @template V value type
 * @template A action type
 */
export type ActionFactory<V, A extends Action> = V extends void ?
  // https://stackoverflow.com/questions/55646272/conditional-method-parameters-based-on-generic-type
  () => A : // nullary
  (value: V) => A; // unary

/**
 * Factory method that takes a value and creates a @type {ThunkCommand}.
 *
 * @template V value type
 * @template A action type
 */
export type ThunkCommandFactory<V, A extends Action> = V extends void ?
  () => ThunkCommand<A> : // nullary
  (value: V) => ThunkCommand<A>; // unary

export interface AppState {
  readonly serverInfo: ServerInfo;
  readonly messages: Message[];
  readonly plan: RoutingPlan;
  readonly connectionStatus: WebSocketConnectionStatus;
  readonly demo: Demo;
  readonly userViewport: UserViewport;
}
