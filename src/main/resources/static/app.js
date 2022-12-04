var stompClient = null;
var username;
var scores;

function onload()
{
    console.log("loaded")
    $("#gameplay-div").hide();
    $("#score-div").hide();
    $("#username").hide();
}


function setConnected(connected) {
    $("#join").hide();
    $("#gameplay-div").hide();
    $("#score-div").hide();
    $("#username").show();
    //$("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        //setting our username
        username = frame["headers"]["user-name"]
        //showing our username
        $('#username').text('You are: Player ' + (parseInt(username)+1) + ". Waiting for all players to join.");
        stompClient.subscribe('/topic/score', function (score) {
            receiveScore(JSON.parse(score.body));
        });
        stompClient.subscribe('/user/queue/message', function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        });
        stompClient.subscribe('/user/queue/username', function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        });
        stompClient.subscribe('/user/queue/card', function (card) {
            receiveCard(JSON.parse(card.body));
        });
        // We now say hello to the server so that we get added to the list of players
        sendHello();
        //TODO Number of cards remaining
    });
}

function receiveCard(cardJson)
{
    //showing our hand
    var list = document.getElementById("hand-list")
    var card = document.createElement('li');
    card.appendChild(document.createTextNode(cardJson["rank"] + cardJson["suit"] + " "));
    if (currentPlayer == username)
    {
        btn = document.createElement('BUTTON');
        text = document.createTextNode("Play");
        btn.appendChild(text);
        btn.addEventListener("click", playCard);
        btn.card = cardJson["rank"] + cardJson["suit"];
        card.appendChild(btn);
    }
    list.appendChild(card);
}

function playCard(evt)
{
    console.log("we want to play card " + evt.currentTarget.card)
}

function receiveScore(scoreJson)
{
    $("#gameplay-div").show();
    $("#score-div").show();

    //setting scores
    var i = 0;
    scoreJson["scores"].forEach(function(score)
    {
        if (username == i)
            $(`#p${i+1}-score`).text(`Player ${i+1}: ` + scoreJson["scores"][i] + " (You)");
        else
            $(`#p${i+1}-score`).text(`Player ${i+1}: ` + scoreJson["scores"][i]);
        i++;
    })

    //Updating turn info
    currentPlayer = scoreJson["currentPlayer"]
    nextPlayer = (currentPlayer + 1)%4;
    $('#turn-header').text("It is currently Player " + (currentPlayer+1) + "'s turn. It will be Player " + (nextPlayer+1) + "'s turn next.")

    $('#username').text('You are: Player ' + (parseInt(username)+1));
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendHello() {
    stompClient.send("/app/hello", {}, JSON.stringify({'content': username}));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    //$( "#join" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});