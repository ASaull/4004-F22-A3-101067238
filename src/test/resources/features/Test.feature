Feature: Multi-player game scoring
  For multi-player tests we run through several turns and
  assert whether a player has won, and/or what their scores are

  Background:
    Given Player 1 connects to the websocket
    And Player 2 connects to the websocket

  Scenario: TestMultiplePlayers
    When Player 1 sends the name 'Aidan'
    Then Player 1 sees 'Hello, Aidan, you are player 1'
    And Player 2 does not see 'Hello, Aidan, you are player 1'
    When Player 2 sends the name 'Owen'
    And Player 2 sees 'Hello, Owen, you are player 2'
    And Player 1 does not see 'Hello, Owen, you are player 2'