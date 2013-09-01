package cuina.ks;
 
public interface BattleMenu
{
    /**
     * Meldet dem Kampfmenü eine Anfrage auf eine Aktion für das übergebene BattleObject.
     * Die Anfrage muss mit dem Aufruf der Methode {@link Battle#menuCallback(BattleObject)} abgeschlossen werden.
     * Änderungen an dem übergebenen BattleObject sind während der laufenden Anfrage jederzeit möglich.
     * <P>
     * Das Menü muss je nach KS-Konfiguration in der Lage sein mehrere Anfragen parallel zu verwalten.
     * Die Reihenfolge der Antworten kann dabei beliebig sein.
     * <p>
     * @param battle Kontext vom Kampfsystem.
     * @param battler Anfragender Kämpfer.
     */
    public void requestInput(Battle battle, BattleObject battler);
    
    /**
     * Meldet dem Kampfmenü einen Abbruch einer zuvor gestellten Anfrage, z.B. weil der Kämpfer gestorben ist,
     * oder aus anderem Grund nicht mehr agieren kann.
     * @param battle Kontext vom Kampfsystem.
     * @param battler Anfragender Kämpfer.
     */
    public void cancelRequest(Battle battle, BattleObject battler);
    
    /**
     * Setzt die Sichtbarkeit des Menüs.
     * Wird benutzt um das Menü während der Ausführugn von Zwischensequenzen oder Spezialattaken zu verstecken.
     * Es wird auch bei verstecktem Menü weiterhin {@link #update()} aufgerufen und es liegt allein am Menü,
     * Eingaben währendessen zu verwerfen.
     * @param value Sichtbarkeit <code>true</code>/<code>false</code>
     */
    public void setVisible(boolean value);
    
    /**
     * Aktualisert die Menüanzeige jeden Frame.
     * Hier sollte irgendwann die Antwort des Menüs erfolgen,
     * indem die Methode {@link Battle#menuCallback(BattleObject)} aufgerufen wird.
     */
    public void update();
    
//  /**
//   * Gibt dei Gewählte Aktion zurück, nachdem <code>isFinished</code> true zurückgegeben hat.
//   * @return
//   */
//  public BattleAction getAction();
    
//  /**
//   * Gibt an, ob die Auswahl im Menü abgeschlossen ist.
//   * Wird jeden Frmae aufgerufen, solange das Menü aktiv ist.
//   * @return <code>true</code>, wenn die Auswahl abgeschlossen ist, andernfalls <code>false</code>.
//   */
//  public boolean isFinished();
}