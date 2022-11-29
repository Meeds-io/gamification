/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2022 Meeds Association
 * contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.exoplatform.addons.gamification.liquibase;

import java.sql.CallableStatement;
import java.sql.ResultSet;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;

public class ResetMySQLAutoIncrement implements CustomTaskChange {

  private String tableName;

  @Override
  public String getConfirmationMessage() {
    return "Auto increment counter for table '" + tableName + "' has been reset";
  }

  @Override
  public void setUp() throws SetupException {
    // Not used
  }

  @Override
  public void setFileOpener(ResourceAccessor resourceAccessor) {
    // Not used
  }

  @Override
  public ValidationErrors validate(Database database) {
    // Not used
    return null;
  }

  @Override
  public void execute(Database database) throws CustomChangeException {
    JdbcConnection connection = (JdbcConnection) database.getConnection();
    try {
      connection.attached(database);
      CallableStatement statement = connection.prepareCall("SELECT MAX(id)+1 FROM " + tableName);
      ResultSet indexResults = statement.executeQuery();
      if (!indexResults.next()) {
        return;
      }
      String result = indexResults.getString(1);
      if (result == null) {
        return;
      }
      CallableStatement query = connection.prepareCall("ALTER TABLE GAMIFICATION_RULE AUTO_INCREMENT = " + result);
      query.execute();
      connection.commit();
    } catch (Exception e) {
      throw new CustomChangeException("Error resetting auto increment counter of table " + tableName, e);
    }
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }
}
