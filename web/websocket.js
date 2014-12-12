var wsocket;
function connect() {
   wsocket = new WebSocket("ws://localhost:8080/Utgard/realtime");
   wsocket.onmessage = onMessage;
}
function onMessage(evt) {
   document.getElementById("koordinata").innerHTML = evt.data;
}
window.addEventListener("load", connect, false);