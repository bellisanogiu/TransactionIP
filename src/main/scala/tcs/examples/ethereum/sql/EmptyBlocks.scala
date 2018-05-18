package tcs.examples.ethereum.sql

import java.util.Date

import scalikejdbc._
import tcs.blockchain.BlockchainLib
import tcs.blockchain.ethereum.EthereumSettings
import tcs.db.sql.Table
import tcs.db.{DatabaseSettings, MySQL}


object EmptyBlocks {
  def main(args: Array[String]): Unit = {
    val blockchain = BlockchainLib.getEthereumBlockchain(new EthereumSettings("https://mainnet.infura.io/lGhdnAJw7n56K0xXGP3i:8545"))
    val pg = new DatabaseSettings("ethereum", MySQL, "root", "toor")

    val blockTable = new Table(
      sql"""
          CREATE TABLE IF NOT EXISTS block(
            hash CHARACTER VARYING(100) NOT NULL PRIMARY KEY,
            timestamp TIMESTAMP,
            miner CHARACTER VARYING(100)
          )
         """,
      sql"""
          INSERT INTO block(hash,timestamp, miner) VALUES (?, ?, ?)
         """,
      pg, 1
    )


    blockchain.foreach(block => {
      if (block.height % 100 == 0) {
        println(block.height)
      }

      if (block.txs.isEmpty) {
        blockTable.insert(Seq(block.hash, block.date, block.miner))
      }
    })

    blockTable.close
  }
}
