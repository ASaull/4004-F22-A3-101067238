var stompClient = null;
var username;
var scores;
var isEight = false;
var eightSuit;
var topCard;
var direction;

function onload()
{
    console.log("loaded")
    $("#gameplay-div").hide();
    $("#score-div").hide();
    $("#username").hide();
    $("#leave").hide();
}


function setConnected() {
    $("#join").hide();
    $("#leave").show();
    $("#gameplay-div").hide();
    $("#score-div").hide();
    $("#username").show();
}

function setDisconnected() {
    //removing cards from list
    location.reload();
    $("#join").show();
    $("#leave").hide();
    $("#gameplay-div").hide();
    $("#score-div").hide();
    $("#username").hide();
}

function connect() {
    var socket = new SockJS('/websocket');
    stompClient = Stomp.over(socket);
    stompClient.debug = null
    stompClient.connect({}, function (frame) {
        setConnected();
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
    });
}

function receiveCard(cardJson)
{
    console.log("received card")
    //showing our hand
    var list = document.getElementById("hand-list")
    var card = document.createElement('li');
    card.classList.add('card');
    card.appendChild(document.createTextNode((cardJson["rank"]=='T' ? '10' : cardJson["rank"] ) + cardJson["suit"] + " "));

    btn = document.createElement('BUTTON');
    text = document.createTextNode("Play");
    btn.appendChild(text);
    btn.addEventListener("click", playCard);
    btn.card = cardJson;
    btn.type = 'button';
    btn.id = cardJson["rank"] + cardJson["suit"];
    btn.classList.add('card-button');
    card.appendChild(btn);

    list.appendChild(card);
}

function playCard(evt)
{
    stompClient.send("/app/play", {}, JSON.stringify(evt.currentTarget.card));
}

function receiveScore(scoreJson)
{
    if (scoreJson["reset"])
    {
        console.log("Game over, resetting")
        setDisconnected();
        return;
    }
    $("#gameplay-div").show();
    $("#score-div").show();

    console.log("Got Scores:")
    console.log(scoreJson)

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
    change = direction ? 1 : -1;
    nextPlayer = (currentPlayer + change)%4;
    $('#turn-header').text("It is currently Player " + (currentPlayer+1) + "'s turn. It will be Player " + (nextPlayer+1) + "'s turn next.")

    //Showing top card
    if (scoreJson["topCard"] != null)
    {
        topCard = scoreJson["topCard"]
        $('#top-card-header').text("Top Card: " + (topCard["rank"]=='T' ? '10' : topCard["rank"] ) + topCard["suit"])
    }

    //Showing our player number
    $('#username').text('You are: Player ' + (parseInt(username)+1));

    //Show number cards remaining
    $('#remaining-header').text(scoreJson["remaining"] + " cards remain in the deck")

    // update playable
    updatePlayable(scoreJson["currentPlayer"]);

    //update direction
    direction = scoreJson["direction"];
}

function isPlayable(card, currentPlayer)
{
    if (currentPlayer != username)
        return;
    // we check if this card is playable.
    if (card["rank"] == 8) //eight always playable
    {
        return true;
    }
    if (isEight && eightSuit == card["suit"]) // playable if eight set to this suit
    {
        return true;
    }
    if (card["rank"] == topCard["rank"]
        || card["suit"] == topCard["suit"]) //otherwise we check both
    {
        return true;
    }
    return false;
}

function updatePlayable(currentPlayer)
{
    cards = document.getElementsByClassName('card');
    Array.from(cards).forEach(function(cardli)
    {
        button = cardli.querySelector('.card-button');
        var card = button.card;
        console.log(button);
        if (isPlayable(card, currentPlayer)) // we remove the button if it is not playable
            button.style.visibility='visible'
        else
            button.style.visibility='hidden'
    });
}

function disconnect() {
    sendGoodbye();
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
    setDisconnected()
}

function sendHello() {
    // we send nothing
    stompClient.send("/app/hello", {}, {});
}

function sendGoodbye()
{
    stompClient.send("/app/goodbye", {}, {});
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
});