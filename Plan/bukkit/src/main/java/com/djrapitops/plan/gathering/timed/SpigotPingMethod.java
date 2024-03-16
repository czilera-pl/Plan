/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2018
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.djrapitops.plan.gathering.timed;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.entity.Player;

public class SpigotPingMethod implements PingMethod {

  private static final Method PLAYER_GET_PING;

  static {
    try {
      PLAYER_GET_PING = Player.class.getDeclaredMethod("getPing");
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  private String reasonForUnavailability;

  @Override
  public boolean isAvailable() {
    try {
      //Only available in Paper
      Class.forName("org.bukkit.entity.Player").getDeclaredMethod("getPing");
      return true;
    } catch (ClassNotFoundException | NoSuchMethodException noSuchMethodEx) {
      reasonForUnavailability = noSuchMethodEx.toString();
      return false;
    }
  }

  @Override
  public int getPing(Player player) {
    if (reasonForUnavailability != null) {
      return -1;
    }

    try {
      return (int) PLAYER_GET_PING.invoke(player);
    } catch (IllegalAccessException | InvocationTargetException e) {
      reasonForUnavailability = e.toString();
      return -1;
    }
  }

  @Override
  public String getReasonForUnavailability() {
    return reasonForUnavailability;
  }
}
