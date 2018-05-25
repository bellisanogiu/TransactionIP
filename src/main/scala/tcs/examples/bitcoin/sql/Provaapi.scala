package tcs.examples.bitcoin.sql

import scalikejdbc._
import tcs.blockchain.BlockchainLib
import tcs.blockchain.bitcoin.{BitcoinSettings, MainNet}
import tcs.db.sql.Table
import tcs.db.{DatabaseSettings, MySQL}
import tcs.utils.DateConverter.convertDate
import tcs.utils._

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
        timestamp TIMESTAMP not null,
        ipRelayedBy varchar(256),
        country varchar(256)
      ) """,
      sql"""insert into transaction(transactionHash, timestamp, ipRelayedBy, country) values (?, ?, ?, ?)""",
      mySQL)

    var a = 0
    blockchain.start(394985).end(394985).foreach(block => {
      block.txs.foreach(tx => {

        //txTable.insert(Seq(tx.hash.toString, block.hash.toString, convertDate(block.date)))
//        println("------------")
//        println("blocco altezza " + block.height)
//        //println("hash " + tx.hash)
//        println("relayed by " + tx.getIP())
//        //tx.getIP()
//         a += 1
//        println("--------- val " + a)
//        Thread.sleep(1000)"https://api.blockcypher.com/v1/btc/main/txs/cbcf51d636b02bedbcdff9b229f2ea2f20e3cb193752714c415b1752c9a0ed32

        //tx.getIP2()
        Thread.sleep(1000)
        println("-------------")

        //println("returned relayed_by " + tx.getIP2())

        val pippo = new IP()
        println("nazione is " + pippo.getCountry(tx.getIP2()))



      }) // end txs

    }) // end block
    txTable.close


  } // end main

} // end object
