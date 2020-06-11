package io.github.Leonardo0013YT.UltraCTW.database;

import io.github.Leonardo0013YT.UltraCTW.interfaces.CTWPlayer;

import java.util.ArrayList;

public class PlayerCTW implements CTWPlayer {

    private ArrayList<Integer> partings = new ArrayList<>(), killeffects = new ArrayList<>(), wineffects = new ArrayList<>(), windances = new ArrayList<>(), killsounds = new ArrayList<>(), taunts = new ArrayList<>(), trails = new ArrayList<>();
    private double coins = 0.0;
    private int woolCaptured = 0, kills = 0, deaths = 0, wins = 0, shots = 0, sShots = 0, walked = 0, played = 0, broken = 0, placed = 0;
    private int level = 1, xp = 0, shopKeeper = 0, winDance = 999999, winEffect = 0, killEffect = 999999, taunt = 0, trail = 999999, parting = 999999, killSound = 999999, assists = 0, kill5 = 0, kill25 = 0, kill50 = 0;

    @Override
    public void addWoolCaptured() {
        this.woolCaptured += 1;
    }

    @Override
    public int getWoolCaptured() {
        return woolCaptured;
    }

    @Override
    public void setWoolCaptured(int woolCaptured) {
        this.woolCaptured = woolCaptured;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int getXp() {
        return xp;
    }

    @Override
    public void setXp(int xp) {
        this.xp = xp;
    }

    @Override
    public int getPlaced() {
        return placed;
    }

    @Override
    public void setPlaced(int placed) {
        this.placed = placed;
    }

    @Override
    public int getDeaths() {
        return deaths;
    }

    @Override
    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    @Override
    public int getWins() {
        return wins;
    }

    @Override
    public void setWins(int wins) {
        this.wins = wins;
    }

    @Override
    public int getShots() {
        return shots;
    }

    @Override
    public void setShots(int shots) {
        this.shots = shots;
    }

    @Override
    public int getsShots() {
        return sShots;
    }

    @Override
    public void setsShots(int sShots) {
        this.sShots = sShots;
    }

    @Override
    public int getWalked() {
        return walked;
    }

    @Override
    public void setWalked(int walked) {
        this.walked = walked;
    }

    @Override
    public int getPlayed() {
        return played;
    }

    @Override
    public void setPlayed(int played) {
        this.played = played;
    }

    @Override
    public int getBroken() {
        return broken;
    }

    @Override
    public void setBroken(int broken) {
        this.broken = broken;
    }

    @Override
    public int getShopKeeper() {
        return shopKeeper;
    }

    @Override
    public void setShopKeeper(int shopKeeper) {
        this.shopKeeper = shopKeeper;
    }

    @Override
    public ArrayList<Integer> getPartings() {
        return partings;
    }

    @Override
    public void setPartings(ArrayList<Integer> partings) {
        this.partings = partings;
    }

    @Override
    public ArrayList<Integer> getKilleffects() {
        return killeffects;
    }

    @Override
    public void setKilleffects(ArrayList<Integer> killeffects) {
        this.killeffects = killeffects;
    }

    @Override
    public ArrayList<Integer> getWineffects() {
        return wineffects;
    }

    @Override
    public void setWineffects(ArrayList<Integer> wineffects) {
        this.wineffects = wineffects;
    }

    @Override
    public ArrayList<Integer> getWindances() {
        return windances;
    }

    @Override
    public void setWindances(ArrayList<Integer> windances) {
        this.windances = windances;
    }

    @Override
    public ArrayList<Integer> getKillsounds() {
        return killsounds;
    }

    @Override
    public void setKillsounds(ArrayList<Integer> killsounds) {
        this.killsounds = killsounds;
    }

    @Override
    public ArrayList<Integer> getTaunts() {
        return taunts;
    }

    @Override
    public void setTaunts(ArrayList<Integer> taunts) {
        this.taunts = taunts;
    }

    @Override
    public ArrayList<Integer> getTrails() {
        return trails;
    }

    @Override
    public void setTrails(ArrayList<Integer> trails) {
        this.trails = trails;
    }

    @Override
    public double getCoins() {
        return coins;
    }

    @Override
    public void setCoins(double coins) {
        this.coins = coins;
    }

    @Override
    public void addCoins(double coins) {
        this.coins += coins;
    }

    @Override
    public void removeCoins(double coins) {
        this.coins -= coins;
    }

    @Override
    public int getKills() {
        return kills;
    }

    @Override
    public void setKills(int kills) {
        this.kills = kills;
    }

    @Override
    public int getWinDance() {
        return winDance;
    }

    @Override
    public void setWinDance(int winDance) {
        this.winDance = winDance;
    }

    @Override
    public int getWinEffect() {
        return winEffect;
    }

    @Override
    public void setWinEffect(int winEffect) {
        this.winEffect = winEffect;
    }

    @Override
    public int getKillEffect() {
        return killEffect;
    }

    @Override
    public void setKillEffect(int killEffect) {
        this.killEffect = killEffect;
    }

    @Override
    public int getTaunt() {
        return taunt;
    }

    @Override
    public void setTaunt(int taunt) {
        this.taunt = taunt;
    }

    @Override
    public int getTrail() {
        return trail;
    }

    @Override
    public void setTrail(int trail) {
        this.trail = trail;
    }

    @Override
    public int getParting() {
        return parting;
    }

    @Override
    public void setParting(int parting) {
        this.parting = parting;
    }

    @Override
    public int getKillSound() {
        return killSound;
    }

    @Override
    public void setKillSound(int killSound) {
        this.killSound = killSound;
    }

    @Override
    public int getAssists() {
        return assists;
    }

    @Override
    public void setAssists(int assists) {
        this.assists = assists;
    }

    @Override
    public int getKill5() {
        return kill5;
    }

    @Override
    public void setKill5(int kill5) {
        this.kill5 = kill5;
    }

    @Override
    public int getKill25() {
        return kill25;
    }

    @Override
    public void setKill25(int kill25) {
        this.kill25 = kill25;
    }

    @Override
    public int getKill50() {
        return kill50;
    }

    @Override
    public void setKill50(int kill50) {
        this.kill50 = kill50;
    }

    @Override
    public void addAssists(int assists) {
        this.assists += assists;
    }

    @Override
    public void addKill5(int kill5) {
        this.kill5 += kill5;
    }

    @Override
    public void addKill25(int kill25) {
        this.kill25 = kill25;
    }

    @Override
    public void addKill50(int kill50) {
        this.kill50 += kill50;
    }

    @Override
    public void addKillEffects(int id) {
        this.killeffects.add(id);
    }

    @Override
    public void addKillSounds(int id) {
        this.killsounds.add(id);
    }

    @Override
    public void addPartings(int id) {
        this.partings.add(id);
    }

    @Override
    public void addTaunts(int id) {
        this.taunts.add(id);
    }

    @Override
    public void addTrails(int id) {
        this.trails.add(id);
    }

    @Override
    public void addWinDances(int id) {
        this.windances.add(id);
    }

    @Override
    public void addWinEffects(int id) {
        this.wineffects.add(id);
    }

}