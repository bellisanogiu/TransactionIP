package tcs.examples.bitcoin.sql

import scalikejdbc._
import tcs.blockchain.BlockchainLib
import tcs.blockchain.bitcoin.{BitcoinSettings, MainNet}
import tcs.db.sql.Table
import tcs.db.{DatabaseSettings, MySQL}
import tcs.utils.DateConverter.convertDate

/**
  * Created by Livio on 14/06/2017.
  */
object Provaapi {
  def main(args: Array[String]): Unit = {

    val blockchain = BlockchainLib.getBitcoinBlockchain(new BitcoinSettings("alice", "8ak1gI25KFTvjovL3gAM967mies3E=", "8332", MainNet))
    val mySQL = new DatabaseSettings("mioesempio", MySQL, "alice", "Djanni74!")


    val txTable = new Table(sql"""
      create table if not exists transaction(
        txid int(10) unsigned auto_increment not null primary key,
        transactionHash varchar(256) not null,
        blockHash varchar(256) not null,
        timestamp TIMESTAMP not null
      ) """,
      sql"""insert into transaction(transactionHash, blockHash, timestamp) values (?, ?, ?)""",
      mySQL)

    blockchain.end(500000).foreach(block => {
      block.txs.foreach(tx => {

        txTable.insert(Seq(tx.hash.toString, block.hash.toString, convertDate(block.date)))
        println("INIZIO")


        tx.getIP()
        println("CONCLUSO")


      }) // end txs

    }) // end block
    txTable.close


  } // end main

} // end object
