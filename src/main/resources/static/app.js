var stompClient = null;
var username;
var scores;
var isEight = false;
var eightSuit;
var topCard;

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
    stompClient.debug = null
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
    });
}

function receiveCard(cardJson)
{
    //showing our hand
    var list = document.getElementById("hand-list")
    var card = document.createElement('li');
    card.classList.add('card');
    card.appendChild(document.createTextNode((cardJson["rank"]=='T' ? '10' : cardJson["rank"] ) + cardJson["suit"] + " "));
    if (currentPlayer == username)
    {
        btn = document.createElement('BUTTON');
        text = document.createTextNode("Play");
        btn.appendChild(text);
        btn.addEventListener("click", playCard);
        btn.card = cardJson;
        btn.classList.add('card-button');
        card.appendChild(btn);
    }
    list.appendChild(card);
}

function playCard(evt)
{
    console.log("we want to play card " + evt.currentTarget.card["rank"] + evt.currentTarget.card["suit"])
}

function receiveScore(scoreJson)
{
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
    nextPlayer = (currentPlayer + 1)%4;
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
    updatePlayable();
}

function isPlayable(card)
{
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

function updatePlayable()
{
    cards = document.getElementsByClassName('card');
    Array.from(cards).forEach(function(cardli)
    {
        button = cardli.querySelector('.card-button');
        card = button.card;
        console.log(button);
        if (isPlayable(card)) // we remove the button if it is not playable
            button.style.visibility='visible'
        else
            button.style.visibility='hidden'
    });
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