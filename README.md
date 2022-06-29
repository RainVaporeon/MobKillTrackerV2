# MobKillTrackerV2
Tracks totem stats for mythic farms

## Usage
Everything's automated, timer starts when a totem placement is detected; stops when exactly 5 minutes has passed (due to totem expiration message will not always appear)

## Commands
Commands are all debug-only, if you would like to toggle the mod, try `/mkt-debug toggle`.

## Debug commands
Command is always /mkt-debug (or /mktd for short)

Commands are as follows:

- test: Starts a 30-second test, especially useful for testing.
- log: Starts a generic log including item and mob kill detections.
- adv: Starts an advanced log including all scanned entities and their data.
- end: Ends current scan and dumps result.
- recollect: Recollects data from the API.
- toggle: Toggles mod

## Builds

aaaa IntellIJ IDEA with Java 8u211
