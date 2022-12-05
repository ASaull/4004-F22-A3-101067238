Feature: Multi-player game scoring
  For multi-player tests we run through several turns and
  assert whether a player has won, and/or what their scores are

  Background:
    Given The game has been started and all players have joined

  Scenario: testConnect
    Then player 1 sees 'You are: Player 1'
    And  player 2 sees 'You are: Player 2'
    And  player 3 sees 'You are: Player 3'
    And  player 4 sees 'You are: Player 4'

  Scenario: testRow41
    Given top card is '5C'
    And player 1 has '3C' in their hand
    When player 1 plays '3C'
    Then current player is player 2

  Scenario: testRow43
    Given top card is '2H'
    And player 1 has 'AH' in their hand
    When player 1 plays 'AH'
    Then current player is player 4
    And next player is player 3

  Scenario: testRow44
    Given top card is 'KC'
    And player 1 has 'QC' in their hand
    When player 1 plays 'QC'
    Then current player is player 3
    And player 2 is notified that they missed their turn

  Scenario: testRow45
    Given top card is 'KC'
    And it is player 4s turn
    And player 4 has '3C' in their hand
    When player 4 plays '3C'
    Then current player is player 1

  Scenario: testRow47
    Given top card is '3H'
    And it is player 4s turn
    And player 4 has 'AH' in their hand
    When player 4 plays 'AH'
    Then current player is player 3
    And next player is player 2
    Given player 3 has '7H' in their hand
    When player 3 plays '7H'
    Then current player is player 2