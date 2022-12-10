var stompClient = null;
var username;
var scores;
var isEight = false;
var eightSuit;
var topCard;
var direction = true;
var numDraws = 0;
var currentPlayer;
var lastAdded;
var gameOverBuffer = false;
var roundOverBuffer = false;
var lowestDeckSizeSeen = 52;

function onload()
{
    console.log("loaded")
    $("#gameplay-div").hide();
    $("#score-div").hide();
    $("#username").hide();
    $("#leave").hide();
    $('#eight-div').hide();
    $("#draw-button").prop('disabled', true);
    $("#pass-button").prop('disabled', true);
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
        stompClient.subscribe('/user/queue/username', function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        });
        stompClient.subscribe('/user/queue/card', function (card) {
            receiveCard(JSON.parse(card.body));
        });
        stompClient.subscribe('/user/queue/message', function (message) {
            processMessage(message.body);
        });
        // We now say hello to the server so that we get added to the list of players
        sendHello();
    });
    gameOverBuffer = false;
}

function processMessage(message)
{
    if (message == 'empty')
    {
        var cards = document.querySelectorAll(".card");
        console.log("cards");
        console.log(cards);
        cards.forEach(function(card)
        {
            card.remove();
        });
    }
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
    lastAdded = cardJson;
    updatePlayable();
}

function playCard(evt)
{
    if (evt.currentTarget.card["rank"] == '8')
    {
        $('#eight-div').show();
        evt.currentTarget.parentElement.remove();
        numDraws = 0;
        return;
    }
    evt.currentTarget.parentElement.remove();
    numDraws = 0;
    stompClient.send("/app/play", {}, JSON.stringify(evt.currentTarget.card));
    roundOverBuffer = false;
}

function drawCard()
{
    console.log("Drawing")
    numDraws++;
    stompClient.send("/app/draw", {}, {})
}

function sendSuit(suit)
{
    var cardJson = {};
    cardJson["suit"] = suit;
    cardJson["rank"] = '8';
    stompClient.send("/app/play", {}, JSON.stringify(cardJson));
    $('#eight-div').hide();
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

    //update direction
    direction = scoreJson["direction"];

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
    console.log(direction)
    console.log(change)
    nextPlayer = (currentPlayer + change)%4;

    skippedMessage = "";
    if (scoreJson["skipped"] == username)
    {
        // we were skipped
        skippedMessage = "  Your turn was skipped!";
    }
    $('#turn-header').text("It is currently Player " + (currentPlayer+1) + "'s turn. It will be Player " + (nextPlayer+1) + "'s turn next." + skippedMessage)

    //Showing top card
    if (scoreJson["topCard"] != null)
    {
        topCard = scoreJson["topCard"]
        $('#top-card-header').text("Top Card: " + (topCard["rank"]=='T' ? '10' : topCard["rank"] ) + topCard["suit"])
    }

    //Showing our player number
    $('#username').text('You are: Player ' + (parseInt(username)+1));

    //Show number cards remaining
    if (scoreJson["remaining"] < lowestDeckSizeSeen)
        lowestDeckSizeSeen = scoreJson["remaining"];
    $('#remaining-header').text(lowestDeckSizeSeen + " cards remain in the deck")

    // update playable
    currentPlayer = scoreJson["currentPlayer"];
    updatePlayable();

    // checking if round over
    if (scoreJson["gameOver"])
    {
        gameOverBuffer = true;
    }
    else if (scoreJson["roundOver"])
    {
        roundOverBuffer = true;
        lowestDeckSizeSeen = 52;
        // removing cards from hand
        var cards = document.getElementsByClassName("card-button");
        console.log("removing cards")
        console.log(cards)
        for (var i = cards.length-1; i >= 0; i--)
        {
            cards.item(i).parentElement.remove();
        }
    }

    // messaging game or round over
    if (gameOverBuffer)
    {
        $('#game-status').text("Game over!")
    }
    else if (roundOverBuffer)
    {
        $('#game-status').text("Round over!")
    }
    else
    {
        $('#game-status').text("")
    }
}

function isPlayable(card)
{
    if (currentPlayer != username)
        return false;
    if (numDraws > 0)
    {
        if (lastAdded != card) // this was not last added
            return false;
    }
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
    var numPlayable = 0
    Array.from(cards).forEach(function(cardli)
    {
        button = cardli.querySelector('.card-button');
        var card = button.card;
        if (isPlayable(card)) // we remove the button if it is not playable
        {
            button.style.visibility='visible'
            numPlayable++;
        }
        else
        {
            button.style.visibility='hidden'
        }
    });
    // base case for draw button, do thsi before complex cases
    if (currentPlayer == username)
        $("#draw-button").prop('disabled', false);
    else
        $("#draw-button").prop('disabled', true);
    if (numPlayable == 0 && numDraws == 3) // nothing can be played, enable pass
    {
        $("#pass-button").prop('disabled', false);
        $("#draw-button").prop('disabled', true);
    }
    else if (numPlayable == 1 && numDraws > 0) // one option, cant draw
    {
        console.log("one option")
        $("#pass-button").prop('disabled', true);
        $("#draw-button").prop('disabled', true);
    }
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

function pass()
{
    numDraws = 0;
    $("#pass-button").prop('disabled', true);
    stompClient.send("/app/pass", {}, {});
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
});