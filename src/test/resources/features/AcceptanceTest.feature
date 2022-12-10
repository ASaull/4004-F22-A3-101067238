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

  Scenario: testRow48
    Given top card is 'KC'
    And it is player 4s turn
    And player 4 has 'QC' in their hand
    When player 4 plays 'QC'
    Then current player is player 2

  Scenario: testRow51
    Given top card is 'KC'
    And player 1 has 'KH' in their hand
    When player 1 plays 'KH'
    Then top card shows 'KH'

  Scenario: testRow52
    Given top card is 'KC'
    And player 1 has '7C' in their hand
    When player 1 plays '7C'
    Then top card shows '7C'

  Scenario: testRow53
    Given top card is 'KC'
    And player 1 has '8H' in their hand
    When player 1 plays '8H'
    Then interface prompts player 1 for a new suit

  Scenario: testRow54
    Given top card is 'KC'
    And player 1 has '5S' in their hand
    Then player 1 cannot play '5S'

  Scenario: testRow58
    Given top card is '7C'
    When player 1 has exactly '3H' as their hand
    And player 1 must draw, gets '6C'
    Then player 1 must play '6C'

  Scenario: testRow59
    Given top card is '7C'
    When player 1 has exactly '3H' as their hand
    And player 1 must draw, gets '6D'
    And player 1 must draw, gets '5C'
    Then player 1 must play '5C'

  Scenario: testRow60
    Given top card is '7C'
    When player 1 has exactly '3H' as their hand
    And player 1 must draw, gets '6D'
    And player 1 must draw, gets '5S'
    And player 1 must draw, gets '7H'
    Then player 1 must play '7H'

  Scenario: testRow61
    Given top card is '7C'
    When player 1 has exactly '3H' as their hand
    And player 1 must draw, gets '6D'
    And player 1 must draw, gets '5S'
    And player 1 must draw, gets '4H'
    Then player 1 must pass
    When player 1 passes
    Then top card shows '7C'
    And current player is player 2

  Scenario: testRow62
    Given top card is '7C'
    When player 1 has exactly '3H' as their hand
    And player 1 must draw, gets '6D'
    And player 1 must draw, gets '8H'
    Then player 1 must play '8H'
    And interface prompts player 1 for a new suit

  Scenario: testRow63
    Given top card is '7C'
    When player 1 has exactly 'KS,3C' as their hand
    And player 1 draws, gets '6C'
    Then player 1 must play '6C'

  Scenario: testRow78
    Given top card is '4S'
    And player 1 has exactly '1S' as their hand
    And player 2 has exactly '5S' as their hand
    And player 3 has exactly '8H,JH,6H,KH,KS' as their hand
    And player 4 has exactly '8C,8D,2S' as their hand
    And it is player 2s turn
    When player 2 plays '5S'
    Then the game is over with scores 1 0 86 102

  Scenario: testRow80
    Given player 1 has exactly '4H,7S,5D,6D,9D' as their hand
    And player 2 has exactly '4S,6S,KC,8H,TD' as their hand
    And player 3 has exactly '9S,6C,9C,JD,3H' as their hand
    And player 4 has exactly '7D,JH,QH,KH,5C' as their hand
    And top card is '4D'
    When player 1 plays '4H'
    And player 2 plays '4S'
    And player 3 plays '9S'
    Then player 4 must draw, gets '2C'
    And player 4 must draw, gets '3C'
    And player 4 must draw, gets '4C'
    And player 4 passes
    And current player is player 1
    When player 1 plays '7S'
    And player 2 plays '6S'
    And player 3 plays '6C'
    And player 4 plays '2C'
    Then player 1 must draw, gets 'TH'
    And player 1 must draw, gets 'JC'
    When player 1 plays 'JC'
    And player 2 plays 'KC'
    And player 3 plays '9C'
    And player 4 plays '3C'
    Then player 1 must draw, gets '7C'
    When player 1 plays '7C'
    And player 2 plays '8H'
    And player 2 selects suit 'D'
    And player 3 plays 'JD'
    And player 4 plays '7D'
    And player 1 plays '9D'
    Then player 1 shows hand '5D,6D,TH'
    And player 2 shows hand 'TD'
    And player 3 shows hand '3H'
    And player 4 shows hand 'JH,QH,KH,5C,4C'
    When player 2 plays 'TD'
    Then the round is over with scores 21 0 3 39
    And it is player 2s turn
    Given top card is 'TD'
    And player 1 has exactly '7D,4S,7C,4H,5D' as their hand
    And player 2 has exactly '9D,3S,9C,3H,JC' as their hand
    And player 3 has exactly '3D,9S,3C,9H,5H' as their hand
    And player 4 has exactly '4D,7S,4C,5S,8D' as their hand
    When player 2 plays '9D'
    And player 3 plays '3D'
    And player 4 plays '4D'
    And player 1 plays '4S'
    And player 2 plays '3S'
    And player 3 plays '9S'
    And player 4 plays '7S'
    And player 1 plays '7C'
    And player 2 plays '9C'
    And player 3 plays '3C'
    And player 4 plays '4C'
    And player 1 plays '4H'
    And player 2 plays '3H'
    And player 3 plays '9H'
    And player 4 draws, gets 'KS'
    Then player 4 must draw, gets 'QS'
    And player 4 must draw, gets 'KH'
    When player 4 plays 'KH'
    Then player 1 must draw, gets '6D'
    And player 1 must draw, gets 'QD'
    And player 1 must draw, gets 'JD'
    And player 1 passes
    And player 2 must draw, gets '6S'
    And player 2 must draw, gets 'JS'
    And player 2 must draw, gets 'TS'
    And player 2 passes
    And player 1 shows hand '5D,6D,7D,JD,QD'
    And player 2 shows hand 'JC,6S,TS,JS'
    And player 3 shows hand '5H'
    And player 4 shows hand '5S,8D,KS,QS'
    When player 3 plays '5H'
    Then the game is over with scores 59 36 3 114
