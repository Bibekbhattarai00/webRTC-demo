# 📞 WebRTC Group Video Call Signaling Server (Spring Boot)

This project is a **signaling server** for a group video call application using **WebRTC**. 
It handles user sessions, room management, and signaling (offer/answer/ICE candidate exchange) over **WebSockets with STOMP**, using **in-memory data structures**.

---

## ⚙️ Technologies Used

- **Java + Spring Boot**
- **WebSockets (STOMP protocol)**
- **In-memory data structures (`ConcurrentHashMap`, `Set`)**
- **Frontend**: vanila js + WebRTC (peer-to-peer)

---

## 📡 Features

- 👥 Supports dynamic group video call rooms
- 🔁 In-memory user and room management
- 💬 WebRTC signaling relay: `offer`, `answer`, `candidate`
- 🔔 Real-time join/leave notifications
- 📊 Room statistics available via WebSocket
- 🔌 Handles user disconnection with session tracking
