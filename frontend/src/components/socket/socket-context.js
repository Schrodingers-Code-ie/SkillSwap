import { createContext, useContext } from 'react';

// Shared WebSocket connection, see SocketContext.jsx
export const SocketContext = createContext(null);
export const useSocket = () => useContext(SocketContext);
